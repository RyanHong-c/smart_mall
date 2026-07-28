package com.smartmall.inventory.converter;

import com.smartmall.inventory.entity.Inventory;
import com.smartmall.inventory.vo.InventoryVO;
import org.springframework.stereotype.Component;

@Component
public class InventoryConverter {
    public InventoryVO toVO(Inventory inventory) {
        if (inventory == null) {
            return null;
        }
        InventoryVO vo = new InventoryVO();
        vo.setProductId(inventory.getProductId());
        vo.setStock(inventory.getStock());
        vo.setFrozenStock(inventory.getFrozenStock());
        vo.setAvailableStock(inventory.getStock() - inventory.getFrozenStock());
        return vo;
    }
}