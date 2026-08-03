package com.smartmall.search.service;

import com.smartmall.search.dto.SearchQueryDTO;
import com.smartmall.search.vo.SearchProductVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SearchService {

    List<SearchProductVO> search(SearchQueryDTO dto);

    long count(SearchQueryDTO dto);

    Map<String, Object> searchPage(SearchQueryDTO dto);

    void syncProduct(Long id, String name, String subtitle, Long categoryId, BigDecimal price, Integer stock, Integer sales, String cover, String description, Integer status);

    void deleteProduct(Long id);
}