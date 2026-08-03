package com.smartmall.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.smartmall.inventory.entity.Inventory;

import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface InventoryMapper
        extends BaseMapper<Inventory> {


}