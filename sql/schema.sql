-- ============================================================
-- SmartMall 全量建表脚本（MySQL 8.x）
-- 数据库：smart_mall
-- 说明：包含所有业务表 + Seata AT 模式 undo_log 表
-- ============================================================

CREATE DATABASE IF NOT EXISTS `smart_mall` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `smart_mall`;

-- ----------------------------
-- 用户表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(64)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码（BCrypt 加密）',
    `nickname`    VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    `phone`       VARCHAR(32)  DEFAULT NULL COMMENT '手机号',
    `email`       VARCHAR(64)  DEFAULT NULL COMMENT '邮箱',
    `gender`      TINYINT      NOT NULL DEFAULT 0 COMMENT '性别: 0未知 1男 2女',
    `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1正常',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删 1已删',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 商品分类表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `product_category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name`        VARCHAR(64)  NOT NULL COMMENT '分类名',
    `parent_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '父分类ID（0为顶级）',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1正常',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ----------------------------
-- 商品表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `product` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `category_id` BIGINT        NOT NULL COMMENT '分类ID',
    `name`        VARCHAR(128)  NOT NULL COMMENT '商品名称',
    `subtitle`    VARCHAR(255)  DEFAULT NULL COMMENT '副标题',
    `price`       DECIMAL(10,2) NOT NULL COMMENT '价格',
    `stock`       INT           NOT NULL DEFAULT 0 COMMENT '库存',
    `sales`       INT           NOT NULL DEFAULT 0 COMMENT '销量',
    `cover`       VARCHAR(255)  DEFAULT NULL COMMENT '封面图URL',
    `description` TEXT          COMMENT '商品描述',
    `status`      TINYINT       NOT NULL DEFAULT 1 COMMENT '状态: 0下架 1上架',
    `deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ----------------------------
-- 库存表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `inventory` (
    `id`           BIGINT   NOT NULL AUTO_INCREMENT COMMENT '库存ID',
    `product_id`   BIGINT   NOT NULL COMMENT '商品ID',
    `stock`        INT      NOT NULL DEFAULT 0 COMMENT '可用库存',
    `frozen_stock` INT      NOT NULL DEFAULT 0 COMMENT '冻结库存',
    `create_time`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- ----------------------------
-- 购物车表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `cart` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
    `product_id`  BIGINT   NOT NULL COMMENT '商品ID',
    `quantity`    INT      NOT NULL DEFAULT 1 COMMENT '数量',
    `checked`     TINYINT  NOT NULL DEFAULT 1 COMMENT '是否选中: 0否 1是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ----------------------------
-- 订单表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `orders` (
    `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no`     VARCHAR(64)   NOT NULL COMMENT '订单编号',
    `user_id`      BIGINT        NOT NULL COMMENT '用户ID',
    `total_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '订单总金额',
    `status`       INT           DEFAULT 0 COMMENT '订单状态: 0待支付 1已支付 2已发货 3已完成 4已取消',
    `create_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- 订单明细表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `order_item` (
    `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `order_id`     BIGINT        NOT NULL COMMENT '订单ID',
    `product_id`   BIGINT        NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(128)  NOT NULL COMMENT '商品名称快照',
    `price`        DECIMAL(10,2) NOT NULL COMMENT '商品单价快照',
    `quantity`     INT           NOT NULL COMMENT '数量',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ----------------------------
-- 支付表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `payment` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '支付单ID',
    `order_id`    BIGINT        NOT NULL COMMENT '订单ID',
    `order_no`    VARCHAR(64)   NOT NULL COMMENT '订单编号',
    `user_id`     BIGINT        NOT NULL COMMENT '用户ID',
    `amount`      DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    `pay_type`    TINYINT       NOT NULL COMMENT '支付方式: 1支付宝 2微信 3银行卡',
    `pay_no`      VARCHAR(64)   NOT NULL COMMENT '第三方支付流水号',
    `status`      TINYINT       NOT NULL DEFAULT 0 COMMENT '支付状态: 0待支付 1已支付 2失败 3退款中 4已退款',
    `pay_time`    DATETIME      NULL COMMENT '支付时间',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_pay_no` (`pay_no`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付表';

-- ----------------------------
-- 收货地址表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `address` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `user_id`        BIGINT       NOT NULL COMMENT '用户ID',
    `receiver_name`  VARCHAR(64)  NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(32)  NOT NULL COMMENT '收货人手机号',
    `province`       VARCHAR(64)  NOT NULL COMMENT '省份',
    `city`           VARCHAR(64)  NOT NULL COMMENT '城市',
    `district`       VARCHAR(64)  NOT NULL COMMENT '区/县',
    `detail_address` VARCHAR(255) NOT NULL COMMENT '详细地址',
    `is_default`     TINYINT      NOT NULL DEFAULT 0 COMMENT '是否默认: 0否 1是',
    `deleted`        TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- ----------------------------
-- Seata AT 模式 undo_log 表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `undo_log` (
    `branch_id`     BIGINT       NOT NULL COMMENT '分支事务ID',
    `xid`           VARCHAR(128) NOT NULL COMMENT '全局事务ID',
    `context`       VARCHAR(128) NOT NULL COMMENT '上下文',
    `rollback_info` LONGBLOB     NOT NULL COMMENT '回滚信息',
    `log_status`    INT          NOT NULL COMMENT '日志状态',
    `log_created`   DATETIME     NOT NULL COMMENT '创建时间',
    `log_modified`  DATETIME     NOT NULL COMMENT '修改时间',
    `ext`           VARCHAR(128) DEFAULT NULL COMMENT '扩展字段',
    PRIMARY KEY (`branch_id`),
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Seata AT 模式 undo_log';
