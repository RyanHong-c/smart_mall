package com.smartmall.search.service.impl;

import com.smartmall.search.dto.SearchQueryDTO;
import com.smartmall.search.entity.ProductDocument;
import com.smartmall.search.repository.ProductSearchRepository;
import com.smartmall.search.service.SearchService;
import com.smartmall.search.vo.SearchProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ProductSearchRepository productSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public List<SearchProductVO> search(SearchQueryDTO dto) {
        Criteria criteria = buildCriteria(dto);
        CriteriaQuery query = new CriteriaQuery(criteria);
        SearchHits<ProductDocument> hits = elasticsearchOperations.search(query, ProductDocument.class);
        return hits.getSearchHits().stream()
                .map(hit -> {
                    SearchProductVO vo = new SearchProductVO();
                    BeanUtils.copyProperties(hit.getContent(), vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public long count(SearchQueryDTO dto) {
        Criteria criteria = buildCriteria(dto);
        CriteriaQuery query = new CriteriaQuery(criteria);
        return elasticsearchOperations.count(query, ProductDocument.class);
    }

    @Override
    public Map<String, Object> searchPage(SearchQueryDTO dto) {
        Criteria criteria = buildCriteria(dto);
        CriteriaQuery query = new CriteriaQuery(criteria);

        if (dto.getSort() != null) {
            switch (dto.getSort()) {
                case "price_asc" -> query.addSort(Sort.by("price").ascending());
                case "price_desc" -> query.addSort(Sort.by("price").descending());
                case "sales" -> query.addSort(Sort.by("sales").descending());
                default -> query.addSort(Sort.by("sales").descending());
            }
        }

        int page = dto.getPage() != null ? dto.getPage() : 1;
        int size = dto.getSize() != null ? dto.getSize() : 10;
        query.setPageable(PageRequest.of(page - 1, size));

        SearchHits<ProductDocument> hits = elasticsearchOperations.search(query, ProductDocument.class);

        List<SearchProductVO> vos = hits.getSearchHits().stream()
                .map(hit -> {
                    SearchProductVO vo = new SearchProductVO();
                    BeanUtils.copyProperties(hit.getContent(), vo);
                    return vo;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("records", vos);
        result.put("total", hits.getTotalHits());
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    private Criteria buildCriteria(SearchQueryDTO dto) {
        List<Criteria> criteriaList = new ArrayList<>();

        criteriaList.add(new Criteria("status").is(1));

        if (dto.getKeyword() != null && !dto.getKeyword().isBlank()) {
            criteriaList.add(new Criteria("name").contains(dto.getKeyword())
                    .or(new Criteria("subtitle").contains(dto.getKeyword()))
                    .or(new Criteria("description").contains(dto.getKeyword())));
        }

        if (dto.getCategoryId() != null) {
            criteriaList.add(new Criteria("categoryId").is(dto.getCategoryId()));
        }

        if (dto.getMinPrice() != null) {
            criteriaList.add(new Criteria("price").greaterThanEqual(dto.getMinPrice()));
        }

        if (dto.getMaxPrice() != null) {
            criteriaList.add(new Criteria("price").lessThanEqual(dto.getMaxPrice()));
        }

        Criteria criteria = criteriaList.get(0);
        for (int i = 1; i < criteriaList.size(); i++) {
            criteria = criteria.and(criteriaList.get(i));
        }
        return criteria;
    }

    @Override
    public void syncProduct(Long id, String name, String subtitle, Long categoryId,
                            BigDecimal price, Integer stock, Integer sales,
                            String cover, String description, Integer status) {
        ProductDocument doc = ProductDocument.builder()
                .id(id)
                .name(name)
                .subtitle(subtitle)
                .categoryId(categoryId)
                .price(price)
                .stock(stock)
                .sales(sales)
                .cover(cover)
                .description(description)
                .status(status)
                .build();
        productSearchRepository.save(doc);
        log.info("商品同步到ES成功，productId={}", id);
    }

    @Override
    public void deleteProduct(Long id) {
        productSearchRepository.deleteById(id);
        log.info("商品从ES删除，productId={}", id);
    }
}