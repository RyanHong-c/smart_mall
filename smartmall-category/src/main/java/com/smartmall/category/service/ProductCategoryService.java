package com.smartmall.category.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartmall.category.entity.ProductCategory;
import com.smartmall.category.dto.CategoryAddDTO;
import com.smartmall.category.dto.CategoryUpdateDTO;
import com.smartmall.category.vo.CategoryTreeVO;

import java.util.List;

public interface ProductCategoryService extends IService<ProductCategory> {
    ProductCategory addCategory(CategoryAddDTO dto);

    ProductCategory updateCategory(Long id, CategoryUpdateDTO dto);

    List<ProductCategory> listCategory();

    List<CategoryTreeVO> tree();

    void deleteCategory(Long id);
}