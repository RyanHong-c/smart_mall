package com.smartmall.order.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


import lombok.Data;


import java.math.BigDecimal;



@Data
@TableName("order_item")
public class OrderItem {


    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 订单ID
     */
    private Long orderId;
    /**
     * 商品ID
     */
    private Long productId;
    /**
     * 商品名称快照
     */
    private String productName;
    /**
     * 商品价格快照
     */
    private BigDecimal price;
    /**
     * 数量
     */
    private Integer quantity;
}