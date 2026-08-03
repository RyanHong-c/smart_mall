package com.smartmall.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("inventory")
public class Inventory {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long productId;
    private Integer stock;
    private Integer frozenStock;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}