package com.smartmall.agent.tool;

import com.smartmall.agent.dto.InventoryInfo;
import com.smartmall.agent.dto.OrderInfo;
import com.smartmall.agent.dto.SearchProduct;
import com.smartmall.agent.dto.SearchResult;
import com.smartmall.agent.feign.CartFeignClient;
import com.smartmall.agent.feign.InventoryFeignClient;
import com.smartmall.agent.feign.OrderFeignClient;
import com.smartmall.agent.feign.ProductFeignClient;
import com.smartmall.agent.feign.SearchFeignClient;
import com.smartmall.product.vo.ProductVO;
import com.smartmall.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * ShopTools 单元测试：验证 Agent 工具层的业务逻辑（搜索/库存/下单护栏）
 */
@ExtendWith(MockitoExtension.class)
class ShopToolsTest {

    @Mock ProductFeignClient productFeignClient;
    @Mock InventoryFeignClient inventoryFeignClient;
    @Mock OrderFeignClient orderFeignClient;
    @Mock CartFeignClient cartFeignClient;
    @Mock SearchFeignClient searchFeignClient;

    @InjectMocks
    ShopTools shopTools;

    private ProductVO iphone;

    @BeforeEach
    void setUp() {
        iphone = new ProductVO();
        iphone.setId(1L);
        iphone.setName("iPhone 17 Pro");
        iphone.setPrice(new BigDecimal("8999.00"));
        iphone.setStock(100);
    }

    @Test
    void searchProducts_shouldMapSearchResultToProductInfo() {
        SearchProduct sp = new SearchProduct();
        sp.setId(1L);
        sp.setName("iPhone 17 Pro");
        sp.setPrice(new BigDecimal("8999.00"));
        sp.setStock(100);

        SearchResult sr = new SearchResult();
        sr.setTotal(1L);
        sr.setRecords(List.of(sp));

        when(searchFeignClient.search(eq("手机"))).thenReturn(Result.success(sr));

        List<ProductInfo> result = shopTools.searchProducts("手机");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("iPhone 17 Pro", result.get(0).getName());
        assertEquals(new BigDecimal("8999.00"), result.get(0).getPrice());
    }

    @Test
    void getStock_shouldPreferInventoryService() {
        InventoryInfo inv = new InventoryInfo();
        inv.setProductId(1L);
        inv.setStock(143);
        when(inventoryFeignClient.getInventory(1L)).thenReturn(Result.success(inv));

        Integer stock = shopTools.getStock(1L);

        assertEquals(143, stock);
    }

    @Test
    void getStock_shouldFallbackToProductWhenInventoryEmpty() {
        when(inventoryFeignClient.getInventory(1L)).thenReturn(Result.success(null));
        when(productFeignClient.getById(1L)).thenReturn(Result.success(iphone));

        Integer stock = shopTools.getStock(1L);

        assertEquals(100, stock);
    }

    @Test
    void previewOrder_thenConfirmOrder_shouldCreateOrder() {
        // 预览：生成确认码 + 订单草稿，不落库
        when(productFeignClient.getById(1L)).thenReturn(Result.success(iphone));
        OrderPreview preview = shopTools.previewOrder(1L, 1L, 2);

        assertNotNull(preview);
        assertNotNull(preview.getConfirmCode());
        assertEquals(new BigDecimal("17998.00"), preview.getTotalAmount());

        // 确认：用确认码真正下单
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(30L);
        orderInfo.setOrderNo("193454ca8d044bccabb8832e9f1518e4");
        when(orderFeignClient.create(any())).thenReturn(Result.success(orderInfo));

        OrderInfo created = shopTools.confirmOrder(preview.getConfirmCode());

        assertNotNull(created);
        assertEquals(30L, created.getId());

        // 草稿已被消费，重复确认应返回 null（幂等保护）
        assertNull(shopTools.confirmOrder(preview.getConfirmCode()));
    }

    @Test
    void getProductDetail_shouldReturnNullWhenNotFound() {
        when(productFeignClient.getById(999L)).thenReturn(Result.success(null));
        assertNull(shopTools.getProductDetail(999L));
    }
}
