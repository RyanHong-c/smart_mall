package com.smartmall.payment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartmall.enums.OrderStatus;
import com.smartmall.enums.ResultCode;
import com.smartmall.exception.BusinessException;
import com.smartmall.order.vo.OrderVO;
import com.smartmall.payment.converter.PaymentConverter;
import com.smartmall.payment.dto.PaymentCallbackDTO;
import com.smartmall.payment.dto.PaymentCreateDTO;
import com.smartmall.payment.entity.Payment;
import com.smartmall.payment.enums.PaymentStatus;
import com.smartmall.payment.feign.OrderFeignClient;
import com.smartmall.payment.mapper.PaymentMapper;
import com.smartmall.payment.service.PaymentService;
import com.smartmall.payment.vo.PaymentVO;
import com.smartmall.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    private final PaymentConverter paymentConverter;
    private final OrderFeignClient orderFeignClient;

    @Override
    @Transactional
    public PaymentVO createPayment(PaymentCreateDTO dto) {
        Result<OrderVO> result = orderFeignClient.getById(dto.getOrderId());
        if (result == null || result.getCode() != 200 || result.getData() == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }
        OrderVO order = result.getData();

        if (!OrderStatus.PENDING.getCode().equals(order.getStatus())) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }

        Payment existPayment = lambdaQuery()
                .eq(Payment::getOrderId, dto.getOrderId())
                .eq(Payment::getStatus, PaymentStatus.PENDING.getCode())
                .one();
        if (existPayment != null) {
            return paymentConverter.toVO(existPayment);
        }

        Payment payment = new Payment();
        payment.setOrderId(dto.getOrderId());
        payment.setOrderNo(order.getOrderNo());
        payment.setUserId(order.getUserId());
        payment.setAmount(order.getTotalAmount());
        payment.setPayType(dto.getPayType());
        payment.setPayNo(UUID.randomUUID().toString().replace("-", ""));
        payment.setStatus(PaymentStatus.PENDING.getCode());
        save(payment);

        log.info("支付单创建成功，paymentId={}, orderId={}, amount={}", payment.getId(), dto.getOrderId(), payment.getAmount());
        return paymentConverter.toVO(payment);
    }

    @Override
    @Transactional
    public PaymentVO pay(Long paymentId) {
        Payment payment = getById(paymentId);
        if (payment == null) {
            throw new BusinessException(ResultCode.PAYMENT_NOT_EXIST);
        }

        if (!PaymentStatus.PENDING.getCode().equals(payment.getStatus())) {
            throw new BusinessException(ResultCode.PAYMENT_STATUS_ERROR);
        }

        payment.setStatus(PaymentStatus.SUCCESS.getCode());
        payment.setPayTime(LocalDateTime.now());
        updateById(payment);

        try {
            Map<String, Integer> body = Map.of("status", OrderStatus.PAID.getCode());
            Result<OrderVO> result = orderFeignClient.updateStatus(payment.getOrderId(), body);
            if (result == null || result.getCode() != 200) {
                log.error("更新订单状态失败，orderId={}", payment.getOrderId());
            } else {
                log.info("订单状态已更新为已支付，orderId={}", payment.getOrderId());
            }
        } catch (Exception e) {
            log.error("调用订单服务更新状态异常，orderId={}", payment.getOrderId(), e);
        }

        log.info("支付成功，paymentId={}, orderId={}", paymentId, payment.getOrderId());
        return paymentConverter.toVO(payment);
    }

    @Override
    @Transactional
    public PaymentVO handleCallback(PaymentCallbackDTO dto) {
        Payment payment = lambdaQuery()
                .eq(Payment::getPayNo, dto.getPayNo())
                .one();
        if (payment == null) {
            throw new BusinessException(ResultCode.PAYMENT_NOT_EXIST);
        }

        if (!PaymentStatus.PENDING.getCode().equals(payment.getStatus())) {
            log.warn("重复回调，paymentId={}, currentStatus={}", payment.getId(), payment.getStatus());
            return paymentConverter.toVO(payment);
        }

        if (dto.getStatus() != null && dto.getStatus() == 1) {
            payment.setStatus(PaymentStatus.SUCCESS.getCode());
            payment.setPayTime(LocalDateTime.now());
            updateById(payment);

            try {
                Map<String, Integer> body = Map.of("status", OrderStatus.PAID.getCode());
                orderFeignClient.updateStatus(payment.getOrderId(), body);
            } catch (Exception e) {
                log.error("回调更新订单状态异常", e);
            }
        } else {
            payment.setStatus(PaymentStatus.FAILED.getCode());
            updateById(payment);
        }

        return paymentConverter.toVO(payment);
    }

    @Override
    public PaymentVO getByOrderId(Long orderId) {
        Payment payment = lambdaQuery()
                .eq(Payment::getOrderId, orderId)
                .one();
        if (payment == null) {
            throw new BusinessException(ResultCode.PAYMENT_NOT_EXIST);
        }
        return paymentConverter.toVO(payment);
    }

    @Override
    public PaymentVO getPaymentById(Long id) {
        Payment payment = super.getById(id);
        if (payment == null) {
            throw new BusinessException(ResultCode.PAYMENT_NOT_EXIST);
        }
        return paymentConverter.toVO(payment);
    }
}