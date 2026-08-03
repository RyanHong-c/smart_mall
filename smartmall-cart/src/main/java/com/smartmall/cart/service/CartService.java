package com.smartmall.cart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartmall.cart.dto.CartAddDTO;
import com.smartmall.cart.dto.CartUpdateDTO;
import com.smartmall.cart.entity.Cart;
import com.smartmall.cart.vo.CartVO;

import java.util.List;

public interface CartService extends IService<Cart> {
    /**
     * 加入购物车
     */
    Cart addCart(CartAddDTO dto);

    /**
     * 查询购物车
     */
    List<CartVO> listCart(Long userId);

    /**
     * 修改数量
     */
    Cart updateCart(Long id, CartUpdateDTO dto);

    /**
     * 删除商品
     */
    void deleteCart(Long id);

    /**
     * 清空购物车
     */
    void clearCart(Long userId);
}