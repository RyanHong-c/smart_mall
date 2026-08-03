-- ============================================
-- SmartMall 支付模块建表脚本
-- ============================================

CREATE TABLE IF NOT EXISTS `payment` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '支付单ID',
    `order_id`    BIGINT       NOT NULL COMMENT '订单ID',
    `order_no`    VARCHAR(64)  NOT NULL COMMENT '订单编号',
    `user_id`     BIGINT       NOT NULL COMMENT '用户ID',
    `amount`      DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    `pay_type`    TINYINT      NOT NULL COMMENT '支付方式: 1=支付宝, 2=微信, 3=银行卡',
    `pay_no`      VARCHAR(64)  NOT NULL COMMENT '第三方支付流水号',
    `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT '支付状态: 0=待支付, 1=已支付, 2=支付失败, 3=退款中, 4=已退款',
    `pay_time`    DATETIME     NULL COMMENT '支付时间',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_pay_no` (`pay_no`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付表';