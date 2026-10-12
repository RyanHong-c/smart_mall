# SmartMall Agent 评测集

端到端评测记录（真实调用 DeepSeek + 5 个微服务 + MySQL/Redis/ES，非 mock）。

## 评测环境

- 模型：deepseek-flash
- 基础设施：MySQL、Redis、Nacos、RocketMQ、Seata、Elasticsearch(8.15.3 + IK)
- 微服务：product(8083) / search(8090) / inventory(8085) / cart(8086) / order(8087) / agent(8092)

## 评测用例

| # | 输入 | 预期工具 | 预期结果 | 实际 |
|---|---|---|---|---|
| 1 | 你好，介绍一下你自己 | 无工具 | 中文自我介绍 | ✅ |
| 2 | 帮我查 iPhone 17 Pro 的库存 | getStock | 调用 inventory-service 返回真实库存 | ✅ 返回 143 |
| 3 | 帮我搜索手机相关的商品 | searchProducts | 调用 search-service(ES+IK) | ✅ 命中 3 条商品 |
| 4 | 我要买 2 台 iPhone 17 Pro 帮我下单 | searchProducts → previewOrder | 生成订单预览，不落库 | ✅ 预览 ¥17998 |
| 5 | 确认下单 | confirmOrder | 真实创建订单 | ✅ 订单 id=22 落库 |
| 6 | 它的库存有多少（多轮） | getStock | 理解"它"指上文商品 | ✅ |
| 7 | 帮我加入购物车 | addToCart | 调用 cart-service | ✅ |
| 8 | 查我的订单 | queryOrder | 调用 order-service | ✅ |

## 核心链路验证

**下单闭环**（用例 4+5）：
```
用户："我要买 2 台 iPhone 17 Pro"
→ LLM 调 searchProducts 确认商品
→ LLM 调 previewOrder(userId=1, productId=1, quantity=2)
→ 返回预览：单价 ¥8999 × 2 = ¥17998 + 确认码
→ 用户："确认下单"
→ LLM 调 confirmOrder(确认码)
→ Feign 调 order-service POST /api/order
→ 订单落库：id=22, order_no=b4b3f7ee..., total=17998.00
→ RocketMQ 发 order-created 消息
→ inventory-service 消费扣减库存：148 → 133
```

**写操作护栏验证**：LLM 在用户提供 userId 前不会下单；预览阶段不落库；只有确认后才产生订单。

## 结论

- 工具选择正确率：8/8 ✅
- 多轮上下文保持：✅
- 写操作护栏生效：✅
- 真实数据（非编造）：✅
