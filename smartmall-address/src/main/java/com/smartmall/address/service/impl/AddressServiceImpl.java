package com.smartmall.address.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartmall.address.converter.AddressConverter;
import com.smartmall.address.dto.AddressAddDTO;
import com.smartmall.address.dto.AddressUpdateDTO;
import com.smartmall.address.entity.Address;
import com.smartmall.address.mapper.AddressMapper;
import com.smartmall.address.service.AddressService;
import com.smartmall.address.vo.AddressVO;
import com.smartmall.enums.ResultCode;
import com.smartmall.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    private final AddressConverter addressConverter;

    private static final int MAX_ADDRESS_COUNT = 20;

    @Override
    @Transactional
    public AddressVO add(AddressAddDTO dto) {
        long count = lambdaQuery().eq(Address::getUserId, dto.getUserId()).count();
        if (count >= MAX_ADDRESS_COUNT) {
            throw new BusinessException(ResultCode.ADDRESS_MAX_COUNT);
        }

        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            clearDefault(dto.getUserId());
        }

        Address address = addressConverter.toEntity(dto);
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }
        save(address);

        return addressConverter.toVO(address);
    }

    @Override
    @Transactional
    public AddressVO update(Long id, AddressUpdateDTO dto) {
        Address address = super.getById(id);
        if (address == null) {
            throw new BusinessException(ResultCode.ADDRESS_NOT_EXIST);
        }

        if (dto.getIsDefault() != null && dto.getIsDefault() == 1
                && address.getIsDefault() != 1) {
            clearDefault(address.getUserId());
        }

        address.setReceiverName(dto.getReceiverName());
        address.setReceiverPhone(dto.getReceiverPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetailAddress(dto.getDetailAddress());
        if (dto.getIsDefault() != null) {
            address.setIsDefault(dto.getIsDefault());
        }
        updateById(address);

        return addressConverter.toVO(address);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Address address = super.getById(id);
        if (address == null) {
            throw new BusinessException(ResultCode.ADDRESS_NOT_EXIST);
        }
        removeById(id);
    }

    @Override
    public AddressVO getById(Long id) {
        Address address = super.getById(id);
        if (address == null) {
            throw new BusinessException(ResultCode.ADDRESS_NOT_EXIST);
        }
        return addressConverter.toVO(address);
    }

    @Override
    public List<AddressVO> listByUserId(Long userId) {
        List<Address> list = lambdaQuery()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreateTime)
                .list();
        return list.stream()
                .map(addressConverter::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> listByUserId(Long userId, int page, int size) {
        IPage<Address> iPage = lambdaQuery()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreateTime)
                .page(new Page<>(page, size));

        List<AddressVO> vos = iPage.getRecords().stream()
                .map(addressConverter::toVO)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("records", vos);
        result.put("total", iPage.getTotal());
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    @Override
    @Transactional
    public void setDefault(Long id, Long userId) {
        Address address = lambdaQuery()
                .eq(Address::getId, id)
                .eq(Address::getUserId, userId)
                .one();
        if (address == null) {
            throw new BusinessException(ResultCode.ADDRESS_NOT_EXIST);
        }

        if (address.getIsDefault() == 1) {
            return;
        }

        clearDefault(userId);

        address.setIsDefault(1);
        updateById(address);
    }

    private void clearDefault(Long userId) {
        lambdaUpdate()
                .eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1)
                .set(Address::getIsDefault, 0)
                .update();
    }
}