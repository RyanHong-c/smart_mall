package com.smartmall.address.converter;

import com.smartmall.address.dto.AddressAddDTO;
import com.smartmall.address.entity.Address;
import com.smartmall.address.vo.AddressVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class AddressConverter {

    public Address toEntity(AddressAddDTO dto) {
        Address address = new Address();
        BeanUtils.copyProperties(dto, address);
        return address;
    }

    public AddressVO toVO(Address address) {
        AddressVO vo = new AddressVO();
        BeanUtils.copyProperties(address, vo);
        return vo;
    }
}