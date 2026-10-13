package com.smartmall.address.feign;

import com.smartmall.address.feign.fallback.AddressFeignClientFallback;
import com.smartmall.address.vo.AddressVO;
import com.smartmall.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "address-service", fallback = AddressFeignClientFallback.class)
public interface AddressFeignClient {

    @GetMapping("/api/address/{id}")
    Result<AddressVO> getById(@PathVariable("id") Long id);
}