package com.smartmall.inventory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartmall.inventory.dto.InventoryUpdateDTO;
import com.smartmall.inventory.entity.Inventory;

public interface InventoryService extends IService<Inventory> {
    /**
     * 查询库存
     */
    Inventory getInventory(Long productId);

    /**
     * 初始化库存
     */
    Inventory initInventory(Long productId);

    /**
     * 修改库存
     */
    Inventory updateInventory(Long productId, InventoryUpdateDTO dto);

    /**
     * 增加库存
     */
    void increase(Long productId, Integer count);

    /**
     * 扣减库存
     */
    void decrease(Long productId, Integer count);
}