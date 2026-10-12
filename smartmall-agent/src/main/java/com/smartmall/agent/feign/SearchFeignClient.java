package com.smartmall.agent.feign;

import com.smartmall.agent.dto.SearchResult;
import com.smartmall.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 搜索服务 Feign 客户端（Elasticsearch + IK 中文分词）
 */
@FeignClient(name = "search-service")
public interface SearchFeignClient {

    @GetMapping("/api/search")
    Result<SearchResult> search(@RequestParam(value = "keyword", required = false) String keyword);
}
