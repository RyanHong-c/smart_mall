package com.smartmall.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.smartmall.enums.ResultCode;
import com.smartmall.exception.BusinessException;
import com.smartmall.inventory.entity.Inventory;
import com.smartmall.utils.RedisUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * InventoryServiceImpl 乐观锁扣减测试：验证超卖防护
 */
@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    RedisUtils redisUtils;

    @Spy
    @InjectMocks
    InventoryServiceImpl inventoryService;

    @Test
    void decrease_shouldThrowWhenStockInsufficient() {
        // 拿锁成功，但数据库原子更新失败（stock 不足）
        when(redisUtils.tryLock(anyString(), anyLong(), anyLong())).thenReturn(true);
        doReturn(false).when(inventoryService).update(any(LambdaUpdateWrapper.class));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> inventoryService.decrease(1L, 999));

        assertEquals(ResultCode.INVENTORY_NOT_ENOUGH.getCode(), ex.getCode());
    }

    @Test
    void decrease_shouldSucceedAndEvictCacheWhenStockEnough() {
        when(redisUtils.tryLock(anyString(), anyLong(), anyLong())).thenReturn(true);
        doReturn(true).when(inventoryService).update(any(LambdaUpdateWrapper.class));

        assertDoesNotThrow(() -> inventoryService.decrease(1L, 2));

        // 扣减成功后应删除缓存，避免读到旧库存
        verify(redisUtils).delete("inventory:stock:1");
    }

    @Test
    void decrease_shouldFailWhenCannotAcquireLock() {
        when(redisUtils.tryLock(anyString(), anyLong(), anyLong())).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> inventoryService.decrease(1L, 2));

        assertEquals(ResultCode.FAIL.getCode(), ex.getCode());
    }
}
