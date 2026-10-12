package com.smartmall.agent.dto;

import lombok.Data;

import java.util.List;

/**
 * 搜索结果（对应 search-service 的 searchPage 返回结构）
 */
@Data
public class SearchResult {
    private Long total;
    private Integer page;
    private Integer size;
    private List<SearchProduct> records;
}
