package com.smartmall.utils;


import cn.hutool.core.bean.BeanUtil;
import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtils {

    /**
     * 单个对象转换
     */
    public static <T> T copyObject(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        T target = BeanUtil.toBean(source, targetClass);
        return target;
    }

    /**
     * 列表转换
     */
    public static <T> List<T> copyList(List<?> sourceList, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return List.of();
        }
        return sourceList.stream()
                .map(source -> BeanUtil.toBean(source, targetClass))
                .collect(Collectors.toList());
    }
}