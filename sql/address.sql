-- ============================================
-- SmartMall 地址模块建表脚本
-- ============================================

CREATE TABLE IF NOT EXISTS `address` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `user_id`         BIGINT       NOT NULL COMMENT '用户ID',
    `receiver_name`   VARCHAR(64)  NOT NULL COMMENT '收货人姓名',
    `receiver_phone`  VARCHAR(32)  NOT NULL COMMENT '收货人手机号',
    `province`        VARCHAR(64)  NOT NULL COMMENT '省份',
    `city`            VARCHAR(64)  NOT NULL COMMENT '城市',
    `district`        VARCHAR(64)  NOT NULL COMMENT '区/县',
    `detail_address`  VARCHAR(255) NOT NULL COMMENT '详细地址',
    `is_default`      TINYINT      NOT NULL DEFAULT 0 COMMENT '是否默认地址: 0=否, 1=是',
    `deleted`         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0=未删除, 1=已删除',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';