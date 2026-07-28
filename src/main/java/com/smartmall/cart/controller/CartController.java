package com.smartmall.cart.controller;

import com.smartmall.cart.dto.CartAddDTO;
import com.smartmall.cart.dto.CartUpdateDTO;
import com.smartmall.cart.service.CartService;
import com.smartmall.cart.vo.CartVO;
import com.smartmall.common.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    /**
     * 加入购物车
     */
    @PostMapping
    public Result<?> add(@RequestBody @Valid CartAddDTO dto) {
        return Result.success(cartService.addCart(dto));
    }

    /**
     * 查询购物车
     */
    @GetMapping("/{userId}")
    public Result<List<CartVO>> list(@PathVariable Long userId) {
        return Result.success(cartService.listCart(userId));
    }

    /**
     * 修改数量
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody @Valid CartUpdateDTO dto) {
        return Result.success(cartService.updateCart(id, dto));
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        cartService.deleteCart(id);
        return Result.success();
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear/{userId}")
    public Result<?> clear(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return Result.success();
    }
}