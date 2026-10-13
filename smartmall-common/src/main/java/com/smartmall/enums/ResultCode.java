package com.smartmall.enums;

public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "系统异常"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),

    // =====================
    // 用户模块 1000-1099
    // =====================
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_EXIST(1003, "用户名已存在"),
    USER_DISABLED(1004, "账号已被禁用"),
    PHONE_EXIST(1005, "手机号已存在"),
    EMAIL_EXIST(1006, "邮箱已存在"),
    USER_OLD_PASSWORD_ERROR(1007, "原密码错误"),

    // =====================
    // 商品模块 2000-2099
    // =====================
    PRODUCT_NOT_EXIST(2001, "商品不存在"),
    PRODUCT_STOCK_NOT_ENOUGH(2002, "商品库存不足"),

    // =====================
    // 分类模块 3000-3099
    // =====================
    CATEGORY_NOT_EXIST(3001, "分类不存在"),

    // =====================
    // 库存模块 4000-4099
    // =====================
    INVENTORY_NOT_EXIST(4001, "库存不存在"),
    INVENTORY_NOT_ENOUGH(4002, "库存不足"),

    // =====================
    // 购物车模块 5000-5099
    // =====================
    CART_ITEM_NOT_EXIST(5001, "购物车商品不存在"),
    CART_EMPTY(5002, "购物车为空"),
    CART_PRODUCT_NOT_EXIST(5003, "商品不存在，无法加入购物车"),
    // =====================
    // 订单模块 6000-6099
    // =====================
    ORDER_NOT_EXIST(6001, "订单不存在"),
    ORDER_STATUS_ERROR(6002, "订单状态异常"),
    ORDER_CREATE_FAILED(6003, "订单创建失败"),
    ORDER_CANCEL_FAILED(6004, "订单取消失败"),
    ORDER_DETAIL_NOT_EXIST(6005, "订单详情不存在"),

    // =====================
    // 支付模块 7000-7099
    // =====================
    PAYMENT_NOT_EXIST(7001, "支付单不存在"),
    PAYMENT_STATUS_ERROR(7002, "支付状态异常"),
    PAYMENT_FAILED(7003, "支付失败"),
    PAYMENT_REFUND_FAILED(7004, "退款失败"),

    // =====================
    // 搜索模块 8000-8099
    // =====================
    SEARCH_ERROR(8001, "搜索服务异常"),

    // =====================
    // 地址模块 9000-9099
    // =====================
    ADDRESS_NOT_EXIST(9001, "收货地址不存在"),
    ADDRESS_MAX_COUNT(9002, "收货地址已达上限");

    private final Integer code;

    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}