package com.smartmall.order.mq;

import com.smartmall.message.dto.OrderMessageDTO;
import com.smartmall.utils.TraceContextPropagator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMessageProducer {

    private final RocketMQTemplate rocketMQTemplate;
    private final TraceContextPropagator traceContextPropagator;

    private static final String TOPIC = "order-created";

    public void sendOrderCreated(OrderMessageDTO dto) {
        TraceContextPropagator.TraceContextInfo traceInfo = traceContextPropagator.capture();
        if (traceInfo != null) {
            dto.setTraceId(traceInfo.traceId());
            dto.setSpanId(traceInfo.spanId());
        }
        Message<OrderMessageDTO> message = MessageBuilder
                .withPayload(dto)
                .setHeader("orderId", dto.getOrderId())
                .build();
        rocketMQTemplate.syncSend(TOPIC, message);
        log.info("订单创建消息发送成功，orderId={}, traceId={}", dto.getOrderId(), dto.getTraceId());
    }
}