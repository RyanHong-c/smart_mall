package com.smartmall.payment.converter;

import com.smartmall.payment.entity.Payment;
import com.smartmall.payment.enums.PayType;
import com.smartmall.payment.enums.PaymentStatus;
import com.smartmall.payment.vo.PaymentVO;
import org.springframework.stereotype.Component;

@Component
public class PaymentConverter {

    public PaymentVO toVO(Payment payment) {
        if (payment == null) {
            return null;
        }
        PaymentVO vo = new PaymentVO();
        vo.setId(payment.getId());
        vo.setOrderId(payment.getOrderId());
        vo.setOrderNo(payment.getOrderNo());
        vo.setUserId(payment.getUserId());
        vo.setAmount(payment.getAmount());
        vo.setPayType(payment.getPayType());
        vo.setPayTypeName(PayType.ALIPAY.getCode().equals(payment.getPayType()) ? "支付宝" :
                PayType.WECHAT.getCode().equals(payment.getPayType()) ? "微信支付" : "银行卡");
        vo.setPayNo(payment.getPayNo());
        vo.setStatus(payment.getStatus());
        vo.setStatusName(PaymentStatus.getMessage(payment.getStatus()));
        vo.setPayTime(payment.getPayTime());
        vo.setCreateTime(payment.getCreateTime());
        return vo;
    }
}