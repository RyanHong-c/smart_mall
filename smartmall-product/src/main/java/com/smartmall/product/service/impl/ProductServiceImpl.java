package com.smartmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartmall.product.dto.ProductAddDTO;
import com.smartmall.product.dto.ProductQueryDTO;
import com.smartmall.product.dto.ProductUpdateDTO;
import com.smartmall.product.entity.Product;
import com.smartmall.enums.ResultCode;
import com.smartmall.exception.BusinessException;
import com.smartmall.product.mapper.ProductMapper;
import com.smartmall.product.service.ProductService;
import com.smartmall.utils.BloomFilterService;
import com.smartmall.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final RedisUtils redisUtils;
    private final BloomFilterService bloomFilterService;

    @Override
    @Transactional
    public Product addProduct(ProductAddDTO dto) {
        //准备接收数据
        Product product = new Product();
        //核心搬运动作：把 dto 里的字段值，按"字段名一致"的规则，逐个拷贝到 product 里。
        //内部原理是 Java 反射——遍历 dto 的所有 getter 方法（如 getName()），去 product 里找对应的 setter 方法（如 setName()）调用赋值
        BeanUtils.copyProperties(dto, product);
        //给新建的对象初始化一些数据
        product.setSales(0);
        product.setStatus(1);

        save(product);

        bloomFilterService.add(product.getId());

        return product;
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductUpdateDTO dto) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST);
        }
        BeanUtils.copyProperties(dto, product);
        updateById(product);
        //假设删缓存和更新数据库之间,恰好有另一个请求进来调用了 getProductDetail(id)——这时候缓存刚被删、
        //数据库还没更新完,它读到的是旧数据,然后又把这个旧数据重新写回了缓存。
        // 等你这边 updateById 执行完,缓存里躺着的却是过期的旧数据,而且这次会一直躺到 3600 秒之后才会消失。
        //
        //所以"先改库,再删缓存"是业界公认更安全的顺序,虽然理论上依然存在极小概率的并发问题(这个更深的话题叫 Cache Aside 模式的经典难题,面试常问,
        // 但一般业务场景不需要过度设计),你现在这个顺序已经是标准做法了
        redisUtils.delete("product:detail:" + id);
        return product;
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException(
                    ResultCode.PRODUCT_NOT_EXIST
            );
        }
        product.setStatus(status);
        updateById(product);
        redisUtils.delete("product:detail:" + id);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        removeById(id);
        redisUtils.delete("product:detail:" + id);
    }

    @Override
    public Product getProductDetail(Long id) {
        String key = "product:detail:" + id;
        String lockKey = "lock:product:detail:" + id;

        if (!bloomFilterService.mightContain(id)) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST);
        }

        Product cache = redisUtils.get(key, Product.class);
        if (cache != null) {
            return cache;
        }

        String nullFlag = redisUtils.get(key, String.class);
        if ("NULL".equals(nullFlag)) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST);
        }

        boolean locked = redisUtils.tryLock(lockKey, 3, 10);
        if (locked) {
            try {
                cache = redisUtils.get(key, Product.class);
                if (cache != null) {
                    return cache;
                }

                Product product = getById(id);
                if (product == null) {
                    redisUtils.setNull(key, 60);
                    throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST);
                }
                redisUtils.set(key, product, 3600);
                return product;
            } finally {
                redisUtils.unlock(lockKey);
            }
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return getProductDetail(id);
    }

    @Override
    public Page<Product> pageQuery(ProductQueryDTO dto) {

        Page<Product> page = new Page<>(
                dto.getPage(),
                dto.getSize()
        );

        LambdaQueryWrapper<Product> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.like(
                dto.getKeyword()!=null,
                Product::getName,
                dto.getKeyword()
        );

        wrapper.eq(
                dto.getCategoryId()!=null,
                Product::getCategoryId,
                dto.getCategoryId()
        );

        wrapper.ge(
                dto.getMinPrice()!=null,
                Product::getPrice,
                dto.getMinPrice()
        );

        wrapper.le(
                dto.getMaxPrice()!=null,
                Product::getPrice,
                dto.getMaxPrice()
        );

        return page(page, wrapper);
    }

}