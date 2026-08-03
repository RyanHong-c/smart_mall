package com.smartmall.search.controller;

import com.smartmall.result.Result;
import com.smartmall.search.dto.SearchQueryDTO;
import com.smartmall.search.service.SearchService;
import com.smartmall.search.vo.SearchProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public Result<Map<String, Object>> search(SearchQueryDTO dto) {
        log.info("商品搜索，keyword={}, categoryId={}, page={}", dto.getKeyword(), dto.getCategoryId(), dto.getPage());
        Map<String, Object> result = searchService.searchPage(dto);
        return Result.success(result);
    }

    @PostMapping("/sync/{productId}")
    public Result<Void> sync(@PathVariable Long productId,
                             @RequestParam String name,
                             @RequestParam(required = false) String subtitle,
                             @RequestParam(required = false) Long categoryId,
                             @RequestParam java.math.BigDecimal price,
                             @RequestParam Integer stock,
                             @RequestParam Integer sales,
                             @RequestParam(required = false) String cover,
                             @RequestParam(required = false) String description,
                             @RequestParam Integer status) {
        searchService.syncProduct(productId, name, subtitle, categoryId, price, stock, sales, cover, description, status);
        return Result.success();
    }

    @DeleteMapping("/sync/{productId}")
    public Result<Void> deleteSync(@PathVariable Long productId) {
        searchService.deleteProduct(productId);
        return Result.success();
    }
}