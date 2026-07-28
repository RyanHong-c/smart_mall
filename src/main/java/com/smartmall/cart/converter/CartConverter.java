package com.smartmall.cart.converter;

import com.smartmall.cart.dto.CartAddDTO;
import com.smartmall.cart.entity.Cart;
import com.smartmall.cart.vo.CartVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class CartConverter {
    /**
     * DTO转Entity
     */
    public Cart toEntity(CartAddDTO dto) {
        Cart cart = new Cart();
        BeanUtils.copyProperties(dto, cart);
        return cart;
    }

    /**
     * Entity转VO
     */
    public CartVO toVO(Cart cart) {
        CartVO vo = new CartVO();
        BeanUtils.copyProperties(cart, vo);
        return vo;
    }
}