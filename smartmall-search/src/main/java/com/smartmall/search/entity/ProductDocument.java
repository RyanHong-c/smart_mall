package com.smartmall.search.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "product")
public class ProductDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String name;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String subtitle;

    @Field(type = FieldType.Keyword, index = false)
    private String cover;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String description;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Integer)
    private Integer stock;

    @Field(type = FieldType.Integer)
    private Integer sales;

    @Field(type = FieldType.Integer)
    private Integer status;
}