package com.smartmall.category.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartmall.exception.BusinessException;
import com.smartmall.enums.ResultCode;
import com.smartmall.category.converter.CategoryConverter;
import com.smartmall.category.dto.CategoryAddDTO;
import com.smartmall.category.dto.CategoryUpdateDTO;
import com.smartmall.category.entity.ProductCategory;
import com.smartmall.category.mapper.ProductCategoryMapper;
import com.smartmall.category.service.ProductCategoryService;
import com.smartmall.category.vo.CategoryTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {
    private final CategoryConverter categoryConverter;

    @Override
    @Transactional
    public ProductCategory addCategory(CategoryAddDTO dto) {
        ProductCategory category = categoryConverter.toEntity(dto);
        save(category);
        return category;
    }

    @Override
    @Transactional
    public ProductCategory updateCategory(Long id, CategoryUpdateDTO dto) {
        ProductCategory category = getById(id);
        if (category == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_EXIST);
        }
        BeanUtils.copyProperties(dto, category);
        updateById(category);
        return category;
    }

    @Override
    public List<ProductCategory> listCategory() {
        return list();
    }

    @Override
    public List<CategoryTreeVO> tree() {
        List<ProductCategory> list = list();
        List<CategoryTreeVO> result = new ArrayList<>();
        for (ProductCategory category : list) {
            if(Long.valueOf(0).equals(category.getParentId())){
                CategoryTreeVO node = categoryConverter.toTreeVO(category);
                node.setChildren(getChildren(category.getId(), list));
                result.add(node);
            }
        }
        return result;
    }

    private List<CategoryTreeVO> getChildren(Long parentId, List<ProductCategory> list) {
        List<CategoryTreeVO> children = new ArrayList<>();
        for (ProductCategory category : list) {
            if (parentId.equals(category.getParentId())) {
                CategoryTreeVO node = categoryConverter.toTreeVO(category);
                node.setChildren(getChildren(category.getId(), list));
                children.add(node);
            }
        }
        return children;
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        ProductCategory category = getById(id);
        if (category == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_EXIST);
        }
        removeById(id);
    }
}