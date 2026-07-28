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
import com.smartmall.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final RedisUtils redisUtils;

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

        //与 mapper 和数据库关联
//        ServiceImpl<ProductMapper, Product>已经和 mapper 关联了
        save(product);
        //返回给 controller
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
    //getProductDetail():真正的业务逻辑,"先问缓存,缓存没有再问数据库"
    public Product getProductDetail(Long id) {
        //拼接出这个商品专属的缓存 key,格式是 product:detail:<商品id>,
        //比如商品1就是 product:detail:1
        String key = "product:detail:" + id;
        //先去 Redis 问一下这个 key 有没有对应的数据,如果有,
        //Jackson 会把 JSON 字符串反序列化成一个 Product 对象返回
        Product cache = redisUtils.get(key, Product.class);
        //命中缓存:直接把这个对象返回给调用方(Controller),函数到此结束,
        //根本不会往下执行任何数据库相关代码——这就是为什么你测试时命中缓存的那几次完全没有 SQL 日志的原因
        if (cache != null) {
            return cache;
        }

        //没命中缓存才继续执行：

        //如果数据库里也没有这条记录(id 不存在或者被软删除了),抛出业务异常,提示"商品不存在"。
        //隐患：mysql 压力
        //这里有个隐患(前面提过的"缓存穿透"):如果调用方一直用一个不存在的 id 疯狂请求,
        //由于这里提前 return/throw,永远不会执行到下面的 redisUtils.set(),
        //所以这种"查不到"的结果永远不会被缓存,每次都会直接打到数据库
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST);
        }
        //查到了正常数据,把它存进 Redis,设置 3600 秒(1小时)过期。下次同样 id 的请求,在 1 小时内都能直接命中缓存
        redisUtils.set(key, product, 3600);
        return product;
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