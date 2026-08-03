package com.smartmall.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartmall.result.Result;
import com.smartmall.product.converter.ProductConverter;
import com.smartmall.product.dto.ProductAddDTO;
import com.smartmall.product.dto.ProductQueryDTO;
import com.smartmall.product.dto.ProductUpdateDTO;
import com.smartmall.product.entity.Product;
import com.smartmall.product.service.ProductService;
import com.smartmall.product.vo.ProductVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

//① 前端发 POST /api/product 请求，带 JSON
//        ↓
//② @RequestBody 自动把 JSON 转成 ProductAddDTO 对象
//        ↓
//③ @Valid 触发参数校验（检查 ProductAddDTO 里的 @NotBlank、@NotNull 等注解）
//        ↓ 校验通过
//④ productService.addProduct(dto) → 交给 Service 处理业务逻辑
//回顾之前贴过的代码：new Product() + BeanUtils.copyProperties
//设置默认sales/status + save到数据库）
//        ↓
//⑤ Service 把存好的 Product（Entity，此时已经有数据库生成的id）返回给 Controller
//        ↓
//⑥ productConverter.toVO(product) → 把 Entity 转成 ProductVO
//        ↓
//⑦ Result.success(vo) → 包装成统一响应格式
//        ↓
//⑧ 返回 JSON 给前端


@RestController   //返回的都是 json 数据
@RequestMapping("/api/product")    //url 前缀
@RequiredArgsConstructor
public class ProductController {

    //依赖注入的声明
    //这是在 ProductController 类里声明了两个成员变量（字段），
    //作用是让 Controller 能够调用 Service 层和 Converter 层的方法
    private final ProductService productService;
    private final ProductConverter productConverter;

    /**
     * 新增商品
     */
//    @RequestBody ProductDTO dto
//    @RequestBody：告诉 Spring，把 HTTP 请求体里的 JSON 数据自动转换成 Java 对象
//    ProductDTO dto：转换后的目标对象
//    比如：
//    POST /api/product
//    {
//        "name": "iPhone 15",
//        "price": 5999.00,
//        "categoryId": 3
//    }
//
//    Spring 会自动把这段 JSON 反序列化成一个 ProductDTO 对象，dto.getName() 就等于 "iPhone 15"。
    @PostMapping
    public Result<ProductVO> add(@Valid @RequestBody ProductAddDTO dto) {
        //传给productservice
        //新建一个Product对象接收回传数据
        Product product = productService.addProduct(dto);

        // 第1步：先执行内层，把 product 转成 vo
        //ProductVO vo = productConverter.toVO(product);
        //
        // 第2步：再执行外层，把 vo 包装成 Result
        //Result<ProductVO> result = Result.success(vo);
        //
        // 第3步：return 出去
        //return result;
        return Result.success(productConverter.toVO(product));
    }

    /**
     * 查询商品详情
     */
    @GetMapping("/{id}")
    public Result<ProductVO> getById(@PathVariable Long id) {
        Product product = productService.getProductDetail(id);
        return Result.success(productConverter.toVO(product));
    }

    /**
     * 商品列表
     */
    @GetMapping("/list")
    public Result<List<ProductVO>> list() {
        List<Product> products = productService.list();
        List<ProductVO> result = products.stream()
                .map(productConverter::toVO)
                .collect(Collectors.toList());
        return Result.success(result);
    }

    /**
     * 商品分页查询
     */
    @GetMapping("/page")
    public Result<Page<ProductVO>> page(ProductQueryDTO dto) {
        Page<Product> result = productService.pageQuery(dto);

        Page<ProductVO> voPage = new Page<>();
        voPage.setCurrent(result.getCurrent());
        voPage.setSize(result.getSize());
        voPage.setTotal(result.getTotal());
        voPage.setRecords(
                result.getRecords().stream()
                        .map(productConverter::toVO)
                        .collect(Collectors.toList())
        );

        return Result.success(voPage);
    }

    /**
     * 修改商品
     */
    @PutMapping("/{id}")
    public Result<ProductVO> update(@PathVariable Long id, @Valid @RequestBody ProductUpdateDTO dto) {
        Product product = productService.updateProduct(id, dto);
        return Result.success(productConverter.toVO(product));
    }

    /**
     * 商品上下架
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        productService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

}