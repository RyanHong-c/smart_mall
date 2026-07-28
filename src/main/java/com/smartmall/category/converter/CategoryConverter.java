package com.smartmall.category.converter;

import com.smartmall.category.dto.CategoryAddDTO;
import com.smartmall.category.dto.CategoryUpdateDTO;
import com.smartmall.category.entity.ProductCategory;
import com.smartmall.category.vo.CategoryTreeVO;
import com.smartmall.category.vo.CategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryConverter {
    public ProductCategory toEntity(CategoryAddDTO dto) {
        ProductCategory category = new ProductCategory();
        BeanUtils.copyProperties(dto, category);
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        return category;
    }

    public void updateEntity(ProductCategory category, CategoryUpdateDTO dto) {
        BeanUtils.copyProperties(dto, category);
    }

    public CategoryVO toVO(ProductCategory category) {
        if (category == null) {
            return null;
        }
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setParentId(category.getParentId());
        vo.setSort(category.getSort());
        vo.setStatus(category.getStatus());
        return vo;
    }

    public CategoryTreeVO toTreeVO(ProductCategory category) {
        CategoryTreeVO vo = new CategoryTreeVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setParentId(category.getParentId());
        return vo;
    }

    public List<CategoryTreeVO> toTreeVOList(List<ProductCategory> list) {
        return list.stream()
                .map(this::toTreeVO)
                .collect(Collectors.toList());
    }
}