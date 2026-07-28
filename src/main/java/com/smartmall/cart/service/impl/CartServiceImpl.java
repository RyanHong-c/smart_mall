package com.smartmall.cart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartmall.cart.converter.CartConverter;
import com.smartmall.cart.dto.CartAddDTO;
import com.smartmall.cart.dto.CartUpdateDTO;
import com.smartmall.cart.entity.Cart;
import com.smartmall.cart.mapper.CartMapper;
import com.smartmall.cart.service.CartService;
import com.smartmall.cart.vo.CartVO;
import com.smartmall.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smartmall.enums.ResultCode;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {
    private final CartConverter cartConverter;

    /**
     * 加入购物车
     */
    @Override
    @Transactional
    public Cart addCart(CartAddDTO dto) {
        Cart existCart = lambdaQuery()
                .eq(Cart::getUserId, dto.getUserId())
                .eq(Cart::getProductId, dto.getProductId())
                .one();
        // 已存在，增加数量
        if (existCart != null) {
            existCart.setQuantity(existCart.getQuantity() + dto.getQuantity());
            updateById(existCart);
            return existCart;
        }
        // 不存在，新增
        Cart cart = cartConverter.toEntity(dto);
        cart.setChecked(1);
        save(cart);
        return cart;
    }

    /**
     * 查询购物车
     */
    @Override
    public List<CartVO> listCart(Long userId) {
        List<Cart> list = lambdaQuery()
                .eq(Cart::getUserId, userId)
                .list();
        return list.stream()
                .map(cartConverter::toVO)
                .collect(Collectors.toList());
    }

    /**
     * 修改数量
     */
    @Override
    @Transactional
    public Cart updateCart(Long id, CartUpdateDTO dto) {
        Cart cart = getById(id);
        if(cart == null){
            throw new BusinessException(ResultCode.CART_ITEM_NOT_EXIST);
        }
        BeanUtils.copyProperties(dto, cart);
        updateById(cart);
        return cart;
    }

    /**
     * 删除购物车商品
     */
    @Override
    @Transactional
    public void deleteCart(Long id) {
        removeById(id);
    }

    /**
     * 清空购物车
     */
    @Override
    @Transactional
    public void clearCart(Long userId) {
        lambdaUpdate()
                .eq(Cart::getUserId, userId)
                .remove();
    }
}