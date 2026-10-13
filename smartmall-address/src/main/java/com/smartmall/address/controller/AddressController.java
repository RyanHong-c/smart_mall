package com.smartmall.address.controller;

import com.smartmall.address.dto.AddressAddDTO;
import com.smartmall.address.dto.AddressUpdateDTO;
import com.smartmall.address.service.AddressService;
import com.smartmall.address.vo.AddressVO;
import com.smartmall.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public Result<AddressVO> add(@RequestBody @Valid AddressAddDTO dto) {
        return Result.success(addressService.add(dto));
    }

    @PutMapping("/{id}")
    public Result<AddressVO> update(@PathVariable Long id, @RequestBody @Valid AddressUpdateDTO dto) {
        return Result.success(addressService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        addressService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<AddressVO> getById(@PathVariable Long id) {
        return Result.success(addressService.getById(id));
    }

    @GetMapping("/user/{userId}")
    public Result<?> listByUserId(@PathVariable Long userId,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        if (size > 0) {
            return Result.success(addressService.listByUserId(userId, page, size));
        }
        return Result.success(addressService.listByUserId(userId));
    }

    @PutMapping("/{id}/default")
    public Result<?> setDefault(@PathVariable Long id, @RequestParam Long userId) {
        addressService.setDefault(id, userId);
        return Result.success();
    }
}