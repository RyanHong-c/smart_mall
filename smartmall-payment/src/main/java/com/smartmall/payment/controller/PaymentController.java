package com.smartmall.payment.controller;

import com.smartmall.result.Result;
import com.smartmall.payment.dto.PaymentCallbackDTO;
import com.smartmall.payment.dto.PaymentCreateDTO;
import com.smartmall.payment.service.PaymentService;
import com.smartmall.payment.vo.PaymentVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public Result<PaymentVO> create(@Valid @RequestBody PaymentCreateDTO dto) {
        log.info("创建支付单，orderId={}, payType={}", dto.getOrderId(), dto.getPayType());
        PaymentVO vo = paymentService.createPayment(dto);
        return Result.success(vo);
    }

    @PostMapping("/pay/{paymentId}")
    public Result<PaymentVO> pay(@PathVariable Long paymentId) {
        log.info("执行支付，paymentId={}", paymentId);
        PaymentVO vo = paymentService.pay(paymentId);
        return Result.success(vo);
    }

    @PostMapping("/callback")
    public Result<PaymentVO> callback(@RequestBody PaymentCallbackDTO dto) {
        log.info("支付回调，payNo={}, status={}", dto.getPayNo(), dto.getStatus());
        PaymentVO vo = paymentService.handleCallback(dto);
        return Result.success(vo);
    }

    @GetMapping("/order/{orderId}")
    public Result<PaymentVO> getByOrderId(@PathVariable Long orderId) {
        PaymentVO vo = paymentService.getByOrderId(orderId);
        return Result.success(vo);
    }

    @GetMapping("/{id}")
    public Result<PaymentVO> getById(@PathVariable Long id) {
        PaymentVO vo = paymentService.getPaymentById(id);
        return Result.success(vo);
    }
}