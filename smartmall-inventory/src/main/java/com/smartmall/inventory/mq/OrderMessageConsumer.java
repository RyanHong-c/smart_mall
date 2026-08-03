package com.smartmall.inventory.mq;

import brave.Span;
import com.smartmall.inventory.service.InventoryService;
import com.smartmall.message.dto.OrderMessageDTO;
import com.smartmall.utils.TraceContextPropagator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "order-created",
        consumerGroup = "inventory-order-consumer",
        maxReconsumeTimes = 3
)
public class OrderMessageConsumer implements RocketMQListener<OrderMessageDTO> {

    private final InventoryService inventoryService;
    private final TraceContextPropagator traceContextPropagator;

    @Override
    public void onMessage(OrderMessageDTO message) {
        TraceContextPropagator.TraceContextInfo traceInfo = null;
        if (message.getTraceId() != null) {
            traceInfo = new TraceContextPropagator.TraceContextInfo(message.getTraceId(), message.getSpanId());
        }
        Span span = traceContextPropagator.startChildSpan(traceInfo, "rocketmq-consume-order-created");
        try {
            log.info("收到订单创建消息，orderId={}, traceId={}", message.getOrderId(), message.getTraceId());
            for (OrderMessageDTO.OrderItemMessage item : message.getItems()) {
                inventoryService.decrease(item.getProductId(), item.getQuantity());
                log.info("库存扣减成功，productId={}, quantity={}", item.getProductId(), item.getQuantity());
            }
        } catch (Exception e) {
            log.error("库存扣减失败，orderId={}", message.getOrderId(), e);
            throw e;
        } finally {
            span.finish();
        }
    }
}