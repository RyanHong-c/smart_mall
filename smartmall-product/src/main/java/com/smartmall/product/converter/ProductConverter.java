package com.smartmall.product.converter;

import com.smartmall.product.entity.Product;
import com.smartmall.product.vo.ProductVO;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter {
    public ProductVO toVO(Product product) {
        // 空值保护
        if (product == null) {
            return null;
        }
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setCategoryId(product.getCategoryId());
        vo.setName(product.getName());
        vo.setSubtitle(product.getSubtitle());
        vo.setPrice(product.getPrice());
        vo.setStock(product.getStock());
        vo.setSales(product.getSales());
        vo.setCover(product.getCover());
        vo.setDescription(product.getDescription());
        vo.setStatus(product.getStatus());
        vo.setCreateTime(product.getCreateTime());
        return vo;
    }
}