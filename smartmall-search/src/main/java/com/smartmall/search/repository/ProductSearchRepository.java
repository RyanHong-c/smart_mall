 package com.smartmall.search.repository;

import com.smartmall.search.entity.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {

    List<ProductDocument> findByNameContaining(String keyword);

    List<ProductDocument> findByNameContainingAndCategoryId(String keyword, Long categoryId);
}