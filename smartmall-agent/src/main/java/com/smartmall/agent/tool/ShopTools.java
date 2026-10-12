package com.smartmall.agent.tool;

import com.smartmall.agent.dto.AddCartRequest;
import com.smartmall.agent.dto.CartItemInfo;
import com.smartmall.agent.dto.CreateOrderRequest;
import com.smartmall.agent.dto.InventoryInfo;
import com.smartmall.agent.dto.OrderInfo;
import com.smartmall.agent.dto.SearchProduct;
import com.smartmall.agent.dto.SearchResult;
import com.smartmall.agent.feign.CartFeignClient;
import com.smartmall.agent.feign.InventoryFeignClient;
import com.smartmall.agent.feign.OrderFeignClient;
import com.smartmall.agent.feign.SearchFeignClient;
import com.smartmall.agent.feign.ProductFeignClient;
import com.smartmall.product.vo.ProductVO;
import com.smartmall.result.Result;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 业务工具集：把电商能力封装成 LLM 可调用的工具（Function Calling）。
 *
 * 数据全部来自真实微服务（通过 Nacos + Feign）：
 *   - 商品搜索/详情 -> product-service
 *   - 库存 -> inventory-service（缺数据时回退 product-service）
 *   - 购物车 -> cart-service
 *   - 下单/查单 -> order-service
 *
 * 写操作护栏：下单采用「预览 -> 用户确认 -> 真正下单」两步式，
 * 大模型不能越过用户直接产生订单。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShopTools {

    private final ProductFeignClient productFeignClient;
    private final InventoryFeignClient inventoryFeignClient;
    private final OrderFeignClient orderFeignClient;
    private final CartFeignClient cartFeignClient;
    private final SearchFeignClient searchFeignClient;

    /** 待确认下单草稿：confirmCode -> 草稿 */
    private final Map<String, CreateOrderRequest> orderDrafts = new ConcurrentHashMap<>();

    /** 草稿中记录商品名，供预览展示 */
    private final Map<String, String> draftProductName = new ConcurrentHashMap<>();

    // ==================== 读类工具 ====================

    @Tool("搜索商品：根据关键词（如手机、耳机、品牌名）通过 Elasticsearch 语义检索返回商品列表")
    public List<ProductInfo> searchProducts(String keyword) {
        Result<SearchResult> result = searchFeignClient.search(keyword);
        if (result == null || result.getCode() != 200 || result.getData() == null) {
            log.warn("调用搜索服务失败: {}", result);
            return new ArrayList<>();
        }
        List<SearchProduct> products = result.getData().getRecords();
        if (products == null) {
            return new ArrayList<>();
        }
        return products.stream()
                .map(p -> new ProductInfo(p.getId(), p.getName(), null, p.getPrice(), p.getStock()))
                .collect(Collectors.toList());
    }

    @Tool("查询商品详情：根据商品ID返回名称、价格、库存等信息")
    public ProductInfo getProductDetail(Long productId) {
        Result<ProductVO> result = productFeignClient.getById(productId);
        if (result == null || result.getCode() != 200 || result.getData() == null) {
            log.warn("查询商品详情失败: productId={}", productId);
            return null;
        }
        return toInfo(result.getData());
    }

    @Tool("查询库存：根据商品ID返回当前可用库存数量（优先库存服务）")
    public Integer getStock(Long productId) {
        try {
            Result<InventoryInfo> r = inventoryFeignClient.getInventory(productId);
            if (r != null && r.getCode() == 200 && r.getData() != null && r.getData().getStock() != null) {
                return r.getData().getStock();
            }
        } catch (Exception e) {
            log.warn("查询库存服务失败，回退商品服务: productId={}, err={}", productId, e.getMessage());
        }
        ProductInfo p = getProductDetail(productId);
        return p == null ? 0 : p.getStock();
    }

    @Tool("查询购物车：根据用户ID返回其购物车商品列表")
    public List<CartItemInfo> listCart(Long userId) {
        Result<List<CartItemInfo>> result = cartFeignClient.list(userId);
        if (result == null || result.getCode() != 200 || result.getData() == null) {
            log.warn("查询购物车失败: userId={}", userId);
            return new ArrayList<>();
        }
        return result.getData();
    }

    @Tool("查询订单：根据订单ID返回订单详情（含商品明细、金额、状态）")
    public OrderInfo queryOrder(Long orderId) {
        Result<OrderInfo> result = orderFeignClient.getById(orderId);
        if (result == null || result.getCode() != 200 || result.getData() == null) {
            log.warn("查询订单失败: orderId={}", orderId);
            return null;
        }
        return result.getData();
    }

    // ==================== 写类工具（带护栏） ====================

    @Tool("加入购物车：把指定商品加入用户购物车")
    public String addToCart(Long userId, Long productId, Integer quantity) {
        AddCartRequest req = new AddCartRequest();
        req.setUserId(userId);
        req.setProductId(productId);
        req.setQuantity(quantity == null ? 1 : quantity);
        Result<CartItemInfo> result = cartFeignClient.add(req);
        if (result == null || result.getCode() != 200) {
            return "加入购物车失败：" + (result == null ? "无响应" : result.getMessage());
        }
        return "已加入购物车：商品ID=" + productId + "，数量=" + req.getQuantity();
    }

    @Tool("生成下单预览：返回待确认的订单方案（含确认码），不真正下单")
    public OrderPreview previewOrder(Long userId, Long productId, Integer quantity) {
        ProductInfo product = getProductDetail(productId);
        if (product == null) {
            return new OrderPreview(null, "商品不存在", userId, productId, null, null, quantity, null);
        }
        int qty = quantity == null || quantity <= 0 ? 1 : quantity;
        BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(qty));

        String confirmCode = UUID.randomUUID().toString().replace("-", "");
        CreateOrderRequest req = new CreateOrderRequest();
        req.setUserId(userId);
        CreateOrderRequest.Item item = new CreateOrderRequest.Item();
        item.setProductId(productId);
        item.setQuantity(qty);
        req.setItems(List.of(item));
        orderDrafts.put(confirmCode, req);
        draftProductName.put(confirmCode, product.getName());

        return new OrderPreview(
                confirmCode,
                "请向用户确认订单内容，用户同意后使用 confirmOrder 工具正式下单",
                userId, productId, product.getName(), product.getPrice(), qty, total);
    }

    @Tool("确认下单：使用预览返回的确认码正式创建订单（仅在用户明确同意后调用）")
    public OrderInfo confirmOrder(String confirmCode) {
        CreateOrderRequest draft = orderDrafts.remove(confirmCode);
        if (draft == null) {
            return null;
        }
        draftProductName.remove(confirmCode);
        Result<OrderInfo> result = orderFeignClient.create(draft);
        if (result == null || result.getCode() != 200 || result.getData() == null) {
            log.error("下单失败: {}", result);
            return null;
        }
        return result.getData();
    }

    // ==================== 内部工具 ====================

    private ProductInfo toInfo(ProductVO vo) {
        return new ProductInfo(
                vo.getId(),
                vo.getName(),
                vo.getCategoryName(),
                vo.getPrice(),
                vo.getStock()
        );
    }

    private boolean contains(ProductVO p, String keyword) {
        String name = p.getName() == null ? "" : p.getName();
        String category = p.getCategoryName() == null ? "" : p.getCategoryName();
        String subtitle = p.getSubtitle() == null ? "" : p.getSubtitle();
        return name.contains(keyword) || category.contains(keyword) || subtitle.contains(keyword);
    }
}
