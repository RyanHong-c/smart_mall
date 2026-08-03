package com.smartmall.product.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartmall.product.entity.Product;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper
        extends BaseMapper<Product> {

}