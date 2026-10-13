# SmartMall —— LLM Agent 驱动的微服务电商平台

一个从「传统微服务商城」升级为「LLM 智能体驱动的电商平台」的完整项目：Spring Cloud Alibaba 微服务底座 + LangChain4j 智能体层，用户用自然语言即可完成商品搜索、库存查询、购物车、下单等操作。

## 一、项目亮点

- **LLM Agent 编排微服务**：基于 LangChain4j + DeepSeek，Function Calling 把 5 个微服务封装为 8 个工具
- **写操作安全护栏**：下单「预览 → 确认码 → 正式下单」两步式，LLM 不能越过用户直接下单
- **下单强一致**：Seata AT 全局事务同步扣库存 + 乐观锁防超卖
- **多轮记忆持久化**：Redis 存储对话记忆，重启不丢失、多实例共享
- **流式输出**：SSE 打字机效果
- **MCP 协议暴露**：8 个工具通过 MCP 暴露，外部 Agent 客户端（Claude Desktop / Cursor / Codex）可直接调用
- **工程化完备**：单元测试、性能压测（QPS 2680）、Agent 评测（100%）、完整建表脚本、配置外置、Swagger、一键启停

## 二、技术栈

| 层 | 技术 |
|---|---|
| 微服务 | Spring Boot 3.5 / Spring Cloud Alibaba（Nacos、OpenFeign、Sentinel、Seata） |
| 消息 | RocketMQ |
| 存储 | MySQL 8、Redis 7、Elasticsearch 8（IK 中文分词） |
| 智能体 | LangChain4j 1.22 + DeepSeek（Function Calling / ReAct / 记忆 / 流式 / MCP） |
| 可观测 | Micrometer Tracing + Zipkin |
| 前端 | 原生 HTML/CSS/JS（SSE 流式渲染） |

## 三、模块结构

```
smart_mall/
├── smartmall-gateway     # 网关（JWT 鉴权 + 路由）
├── smartmall-auth        # 认证（登录签发 token）
├── smartmall-user        # 用户
├── smartmall-product     # 商品（布隆过滤器 + 缓存）
├── smartmall-category    # 分类
├── smartmall-inventory   # 库存（乐观锁 + Seata）
├── smartmall-cart        # 购物车
├── smartmall-order       # 订单（Seata 全局事务）
├── smartmall-payment     # 支付
├── smartmall-search      # 搜索（ES + IK）
├── smartmall-address     # 收货地址
├── smartmall-security    # Spring Security 公共库
├── smartmall-common      # 公共库（Result/异常/Redis 工具）
├── smartmall-agent       # ★ LLM 智能体（LangChain4j + 8 工具 + MCP）
├── sql/                  # 建表脚本（schema.sql 全量）
└── scripts/              # 评测 / MCP 验证脚本
```

## 四、操作手册

### 1. 前置准备（一次性）

- JDK 17+、Maven 3.9+、Docker Desktop（已启动）
- DeepSeek API Key（或任意 OpenAI 兼容 Key）
- Docker 容器由 `docker-compose.yml` 管理（project: smartmall，容器名 `smartmall-xxx-1`）
- 本机 MySQL 服务在运行（微服务连 `127.0.0.1:3306`，见下文「注意事项」）

### 2. 一键启动

```bash
cd /Users/ryan-hong/IdeaProjects/smart_mall
export DEEPSEEK_API_KEY=sk-你的key

./start-agent.sh      # 启动基础设施 + 5 个微服务 + agent + 前端
```

启动后访问：

| 入口 | 地址 |
|---|---|
| **智能导购前端** | http://localhost:8092 |
| Swagger 接口文档 | http://localhost:8092/swagger-ui/index.html |
| Nacos 控制台 | http://localhost:8848/nacos |
| Zipkin 链路追踪 | http://localhost:9411 |
| Sentinel | http://localhost:8080 |
| Elasticsearch | http://localhost:9200 |

### 3. 停止

```bash
./stop-agent.sh      # 停止微服务 + agent
docker compose stop  # 如需同时停止基础设施容器
```

### 4. 手动启动（逐个服务）

```bash
mvn -pl smartmall-product spring-boot:run    # 8083
mvn -pl smartmall-search spring-boot:run     # 8090
mvn -pl smartmall-inventory spring-boot:run  # 8085
mvn -pl smartmall-cart spring-boot:run       # 8086
mvn -pl smartmall-order spring-boot:run      # 8087
DEEPSEEK_API_KEY=sk-xxx mvn -pl smartmall-agent spring-boot:run  # 8092
```

### 5. Agent 使用

- **Web 聊天**：打开 http://localhost:8092，输入自然语言即可（支持多轮、流式、下单护栏）
- **接口调用**：
  ```bash
  curl -X POST http://localhost:8092/api/agent/chat \
    -H "Content-Type: application/json" \
    -d '{"sessionId":"s1","message":"帮我找一款手机"}'
  ```
- **流式接口**：`POST /api/agent/chat/stream`（SSE）

### 6. MCP 使用

8 个工具已暴露为 MCP 协议，可被 Claude Desktop / Cursor / Codex 等调用：

```bash
mvn -pl smartmall-agent -am package -DskipTests
export DEEPSEEK_API_KEY=sk-xxx
python3 scripts/mcp-verify.py     # 验证：initialize 握手 + tools/list 8 工具
```

### 7. 测试 / 评测 / 压测

```bash
# 单元测试（8 用例）
mvn -pl smartmall-agent,smartmall-inventory test

# Agent 端到端评测（正确率）
python3 scripts/agent-eval.py

# 性能压测（缓存命中）
curl -o /dev/null http://127.0.0.1:8083/api/product/1   # 预热
ab -n 2000 -c 100 http://127.0.0.1:8083/api/product/1
```

## 五、配置说明（敏感信息一律环境变量）

| 环境变量 | 说明 | 默认值 |
|---|---|---|
| `DEEPSEEK_API_KEY` | LLM API Key（必填） | 无 |
| `DEEPSEEK_BASE_URL` | LLM 接口地址 | `https://api.deepseek.com/v1` |
| `DEEPSEEK_MODEL` | 模型名 | `deepseek-flash` |
| `MYSQL_PASSWORD` | MySQL 密码 | `1813745788` |
| `JWT_SECRET` | JWT 签名密钥 | `smartmall-smartmall-smartmall-secret-key` |
| `MCP_ENABLED` | 是否以 MCP 模式启动 | `false` |

> ⚠️ 生产环境务必通过环境变量覆盖 `DEEPSEEK_API_KEY`、`MYSQL_PASSWORD`、`JWT_SECRET`，不要使用默认值。

## 六、注意事项（踩坑记录）

1. **本机有两个 MySQL**：微服务连的是本机 `127.0.0.1:3306` 的 mysqld（不是 Docker 容器 mysql）。`docker exec smartmall-mysql-1` 查的是另一个库，会看到旧数据。
2. **Elasticsearch 需 IK 插件**：`search-service` 依赖 `ik_max_word` 分析器。新环境需重装：
   ```bash
   docker exec smartmall-elasticsearch-1 elasticsearch-plugin install --batch \
     https://get.infini.cloud/elasticsearch/analysis-ik/8.15.3
   docker restart smartmall-elasticsearch-1
   ```
3. **端口规划**：gateway 8088 / auth 8081 / user 8082 / product 8083 / category 8084 / inventory 8085 / cart 8086 / order 8087 / search 8090 / agent 8092 / address 8093 / payment 8094（Seata 占用 8091）。

## 七、数据佐证

- 性能：缓存命中 QPS 2680，延迟 1.84ms（对比未命中 7.74ms、穿透拦截 0.99ms）
- Agent 评测：6/6 任务，工具编排正确率 100%
- 详见 `smartmall-agent/BENCHMARK.md`、`smartmall-agent/EVAL.md`
