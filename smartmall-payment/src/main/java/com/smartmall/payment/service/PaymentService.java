package com.smartmall.payment.service;

import com.smartmall.payment.dto.PaymentCallbackDTO;
import com.smartmall.payment.dto.PaymentCreateDTO;
import com.smartmall.payment.entity.Payment;
import com.smartmall.payment.vo.PaymentVO;

public interface PaymentService {

    PaymentVO createPayment(PaymentCreateDTO dto);

    PaymentVO pay(Long paymentId);

    PaymentVO handleCallback(PaymentCallbackDTO dto);

    PaymentVO getByOrderId(Long orderId);

    PaymentVO getPaymentById(Long id);
}