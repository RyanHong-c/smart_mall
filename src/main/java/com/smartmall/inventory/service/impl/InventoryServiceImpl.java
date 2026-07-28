package com.smartmall.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartmall.inventory.dto.InventoryUpdateDTO;
import com.smartmall.inventory.entity.Inventory;
import com.smartmall.inventory.mapper.InventoryMapper;
import com.smartmall.inventory.service.InventoryService;
import com.smartmall.exception.BusinessException;
import com.smartmall.enums.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService {
    @Override
    public Inventory getInventory(Long productId) {
        return getOne(new LambdaQueryWrapper<Inventory>()
                .eq(Inventory::getProductId, productId));
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
        return inventory;
    }

    @Override
    @Transactional
    public Inventory updateInventory(Long productId, InventoryUpdateDTO dto) {
        Inventory inventory = getInventory(productId);
        if (inventory == null) {
            inventory = initInventory(productId);
        }
        inventory.setStock(dto.getStock());
        updateById(inventory);
        return inventory;
    }

    @Override
    @Transactional
    public void increase(Long productId, Integer count) {
        Inventory inventory = getInventory(productId);
        if (inventory == null) {
            inventory = initInventory(productId);
        }
        inventory.setStock(inventory.getStock() + count);
        updateById(inventory);
    }

    @Override
    @Transactional
    public void decrease(Long productId, Integer count) {
        Inventory inventory = getInventory(productId);
        if (inventory == null) {
            throw new BusinessException(ResultCode.INVENTORY_NOT_EXIST);
        }
        int available = inventory.getStock() - inventory.getFrozenStock();
        if (available < count) {
            throw new BusinessException(ResultCode.INVENTORY_NOT_ENOUGH);
        }
        inventory.setStock(inventory.getStock() - count);
        updateById(inventory);
    }
}