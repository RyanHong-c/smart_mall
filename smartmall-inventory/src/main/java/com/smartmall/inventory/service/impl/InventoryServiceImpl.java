package com.smartmall.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartmall.inventory.dto.InventoryUpdateDTO;
import com.smartmall.inventory.entity.Inventory;
import com.smartmall.inventory.mapper.InventoryMapper;
import com.smartmall.inventory.service.InventoryService;
import com.smartmall.exception.BusinessException;
import com.smartmall.enums.ResultCode;
import com.smartmall.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService {

    private final RedisUtils redisUtils;

    private static final String STOCK_KEY_PREFIX = "inventory:stock:";
    private static final String LOCK_KEY_PREFIX = "inventory:lock:";
    private static final long CACHE_TIMEOUT = 600;
    private static final long LOCK_WAIT = 1;
    private static final long LOCK_LEASE = 5;
    private static final int RETRY_TIMES = 3;
    private static final long RETRY_INTERVAL = 50;

    @Override
    public Inventory getInventory(Long productId) {
        String key = STOCK_KEY_PREFIX + productId;
        Inventory cache = redisUtils.get(key, Inventory.class);
        if (cache != null) {
            return cache;
        }
        Inventory inventory = getOne(new LambdaQueryWrapper<Inventory>()
                .eq(Inventory::getProductId, productId));
        if (inventory != null) {
            redisUtils.set(key, inventory, CACHE_TIMEOUT);
        }
        return inventory;
    }

    @Override
    @Transactional
    public Inventory initInventory(Long productId) {
        Inventory inventory = getInventory(productId);
        if (inventory != null) {
            return inventory;
        }
        inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setStock(0);
        inventory.setFrozenStock(0);
        save(inventory);
        redisUtils.set(STOCK_KEY_PREFIX + productId, inventory, CACHE_TIMEOUT);
        return inventory;
    }

    @Override
    @Transactional
    public Inventory updateInventory(Long productId, InventoryUpdateDTO dto) {
        String lockKey = LOCK_KEY_PREFIX + productId;
        boolean locked = redisUtils.tryLock(lockKey, LOCK_WAIT, LOCK_LEASE);
        if (!locked) {
            throw new BusinessException(ResultCode.FAIL);
        }
        try {
            Inventory inventory = getInventory(productId);
            if (inventory == null) {
                inventory = initInventory(productId);
            }
            inventory.setStock(dto.getStock());
            updateById(inventory);
            redisUtils.set(STOCK_KEY_PREFIX + productId, inventory, CACHE_TIMEOUT);
            return inventory;
        } finally {
            redisUtils.unlock(lockKey);
        }
    }

    @Override
    @Transactional
    public void increase(Long productId, Integer count) {
        String lockKey = LOCK_KEY_PREFIX + productId;
        for (int i = 0; i < RETRY_TIMES; i++) {
            if (redisUtils.tryLock(lockKey, LOCK_WAIT, LOCK_LEASE)) {
                try {
                    Inventory inventory = getInventory(productId);
                    if (inventory == null) {
                        inventory = initInventory(productId);
                    }
                    inventory.setStock(inventory.getStock() + count);
                    updateById(inventory);
                    redisUtils.set(STOCK_KEY_PREFIX + productId, inventory, CACHE_TIMEOUT);
                    return;
                } finally {
                    redisUtils.unlock(lockKey);
                }
            }
            try {
                Thread.sleep(RETRY_INTERVAL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BusinessException(ResultCode.FAIL);
            }
        }
        throw new BusinessException(ResultCode.FAIL);
    }

    @Override
    @Transactional
    public void decrease(Long productId, Integer count) {
        String lockKey = LOCK_KEY_PREFIX + productId;
        boolean locked = redisUtils.tryLock(lockKey, LOCK_WAIT, LOCK_LEASE);
        if (!locked) {
            throw new BusinessException(ResultCode.FAIL);
        }
        try {
            // 乐观锁原子扣减：数据库层面保证 stock >= count 才扣减，防止超卖
            LambdaUpdateWrapper<Inventory> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(Inventory::getProductId, productId)
                    .ge(Inventory::getStock, count)
                    .setSql("stock = stock - " + count);
            boolean updated = update(wrapper);
            if (!updated) {
                throw new BusinessException(ResultCode.INVENTORY_NOT_ENOUGH);
            }
            // 扣减成功后删除缓存，避免读到旧库存
            redisUtils.delete(STOCK_KEY_PREFIX + productId);
        } finally {
            redisUtils.unlock(lockKey);
        }
    }
}