package com.smartmall.address.feign.fallback;

import com.smartmall.address.feign.AddressFeignClient;
import com.smartmall.address.vo.AddressVO;
import com.smartmall.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AddressFeignClientFallback implements AddressFeignClient {

    @Override
    public Result<AddressVO> getById(Long id) {
        log.error("地址服务调用失败，触发熔断降级: addressId={}", id);
        return Result.fail(500, "地址服务暂不可用");
    }
}