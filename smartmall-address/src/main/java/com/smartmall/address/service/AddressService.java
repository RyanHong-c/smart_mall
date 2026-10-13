package com.smartmall.address.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartmall.address.dto.AddressAddDTO;
import com.smartmall.address.dto.AddressUpdateDTO;
import com.smartmall.address.entity.Address;
import com.smartmall.address.vo.AddressVO;

import java.util.List;
import java.util.Map;

public interface AddressService extends IService<Address> {

    AddressVO add(AddressAddDTO dto);

    AddressVO update(Long id, AddressUpdateDTO dto);

    void delete(Long id);

    AddressVO getById(Long id);

    List<AddressVO> listByUserId(Long userId);

    Map<String, Object> listByUserId(Long userId, int page, int size);

    void setDefault(Long id, Long userId);
}