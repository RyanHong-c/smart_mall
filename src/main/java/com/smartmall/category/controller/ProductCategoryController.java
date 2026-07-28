package com.smartmall.category.controller;

import com.smartmall.common.Result;
import com.smartmall.category.dto.CategoryAddDTO;
import com.smartmall.category.dto.CategoryUpdateDTO;
import com.smartmall.category.entity.ProductCategory;
import com.smartmall.category.service.ProductCategoryService;
import com.smartmall.category.converter.CategoryConverter;
import com.smartmall.category.vo.CategoryVO;
import com.smartmall.category.vo.CategoryTreeVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class ProductCategoryController {
    private final ProductCategoryService categoryService;
    private final CategoryConverter categoryConverter;

    /**
     * 新增分类
     */
    @PostMapping
    public Result<CategoryVO> add(@Valid @RequestBody CategoryAddDTO dto) {
        ProductCategory category = categoryService.addCategory(dto);
        return Result.success(categoryConverter.toVO(category));
    }

    /**
     * 分类列表
     */
    @GetMapping("/list")
    public Result<List<CategoryVO>> list() {
        List<ProductCategory> list = categoryService.listCategory();
        List<CategoryVO> result = list.stream()
                .map(categoryConverter::toVO)
                .collect(Collectors.toList());
        return Result.success(result);
    }

    /**
     * 分类树
     */
    @GetMapping("/tree")
    public Result<List<CategoryTreeVO>> tree() {
        return Result.success(categoryService.tree());
    }

    /**
     * 修改分类
     */
    @PutMapping("/{id}")
    public Result<CategoryVO> update(@PathVariable Long id, @Valid @RequestBody CategoryUpdateDTO dto) {
        ProductCategory category = categoryService.updateCategory(id, dto);
        return Result.success(categoryConverter.toVO(category));
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}