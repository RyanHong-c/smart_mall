package com.smartmall.address.vo;

import lombok.Data;

@Data
public class AddressVO {

    private Long id;

    private Long userId;

    private String receiverName;

    private String receiverPhone;

    private String province;

    private String city;

    private String district;

    private String detailAddress;

    private Integer isDefault;
}