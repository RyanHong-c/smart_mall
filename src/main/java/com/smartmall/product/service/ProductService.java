package com.smartmall.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smartmall.product.dto.ProductAddDTO;
import com.smartmall.product.dto.ProductQueryDTO;
import com.smartmall.product.dto.ProductUpdateDTO;
import com.smartmall.product.entity.Product;

public interface ProductService extends IService<Product> {

    //链接 controller 和 impl
    Product addProduct(ProductAddDTO dto);

    Product updateProduct(Long id, ProductUpdateDTO dto);

    void updateStatus(Long id, Integer status);

    void deleteProduct(Long id);

    Page<Product> pageQuery(ProductQueryDTO dto);

    Product getProductDetail(Long id);
}