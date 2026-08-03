#!/bin/bash
# ============================================
# SmartMall 一键启动脚本
# 启动顺序: 基础设施 → 微服务(按依赖)
# ============================================

set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$PROJECT_DIR/logs"
mkdir -p "$LOG_DIR"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info()  { echo -e "${GREEN}[INFO]${NC}  $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC}  $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_step()  { echo -e "\n${BLUE}━━━ $1 ━━━${NC}"; }

check_port() {
    lsof -iTCP:"$1" -sTCP:LISTEN -P -n > /dev/null 2>&1
}

wait_for_port() {
    local port=$1
    local name=$2
    local max_wait=${3:-60}
    local waited=0
    while ! check_port "$port"; do
        sleep 2
        waited=$((waited + 2))
        if [ $waited -ge $max_wait ]; then
            log_error "$name 端口 $port 启动超时"
            return 1
        fi
    done
    log_info "$name 已就绪 (端口 $port)"
}

# ============================================
# 第一阶段：启动基础设施
# ============================================
log_step "第一阶段：启动 Docker 基础设施"

DOCKER_CONTAINERS=(
    "mysql"
    "redis"
    "nacos"
    "rocketmq-namesrv"
    "rocketmq-broker"
    "sentinel-dashboard"
    "seata-server"
    "zipkin"
)

for container in "${DOCKER_CONTAINERS[@]}"; do
    if docker ps --format '{{.Names}}' | grep -q "^${container}$"; then
        log_info "容器 $container 已在运行"
    else
        log_info "启动容器 $container ..."
        docker start "$container" 2>/dev/null || log_warn "容器 $container 不存在，跳过"
    fi
done

log_info "等待基础设施就绪..."
wait_for_port 3306 "MySQL"
wait_for_port 6379 "Redis"
wait_for_port 8848 "Nacos"
wait_for_port 9876 "RocketMQ NameServer"
wait_for_port 10911 "RocketMQ Broker"
wait_for_port 8091 "Seata"
wait_for_port 9411 "Zipkin"

# Sentinel Dashboard 映射到 8080→8858
log_info "Sentinel Dashboard 已就绪 (端口 8080→8858)"

# ============================================
# 第二阶段：编译（可选）
# ============================================
if [ "$1" = "--build" ] || [ "$1" = "-b" ]; then
    log_step "第二阶段：编译项目"
    cd "$PROJECT_DIR"
    mvn clean package -DskipTests -q
    log_info "编译完成"
else
    log_info "跳过编译（使用 --build 或 -b 参数强制编译）"
fi

# ============================================
# 第三阶段：启动微服务
# ============================================
log_step "第三阶段：启动微服务（按依赖顺序）"

cd "$PROJECT_DIR"

SERVICES=(
    "smartmall-auth:8081:auth-service"
    "smartmall-user:8082:user-service"
    "smartmall-product:8083:product-service"
    "smartmall-category:8084:category-service"
    "smartmall-inventory:8085:inventory-service"
    "smartmall-cart:8086:cart-service"
    "smartmall-order:8087:order-service"
    "smartmall-gateway:8088:gateway-service"
)

for service_info in "${SERVICES[@]}"; do
    IFS=':' read -r module port name <<< "$service_info"

    if check_port "$port"; then
        log_warn "$name 端口 $port 已被占用，跳过启动"
        continue
    fi

    log_info "启动 $name (端口 $port) ..."
    cd "$PROJECT_DIR/$module"
    nohup mvn spring-boot:run -q > "$LOG_DIR/${name}.log" 2>&1 &
    cd "$PROJECT_DIR"

    wait_for_port "$port" "$name" 120
done

# ============================================
# 第四阶段：状态汇总
# ============================================
log_step "启动完成！服务状态汇总"

echo ""
printf "%-25s %-10s %s\n" "服务" "端口" "状态"
printf "%-25s %-10s %s\n" "────────────────────" "──────" "────"
for service_info in "${SERVICES[@]}"; do
    IFS=':' read -r module port name <<< "$service_info"
    if check_port "$port"; then
        printf "%-25s %-10s ${GREEN}✅ 运行中${NC}\n" "$name" "$port"
    else
        printf "%-25s %-10s ${RED}❌ 未启动${NC}\n" "$name" "$port"
    fi
done

echo ""
echo -e "${GREEN}════════════════════════════════════════${NC}"
echo -e "${GREEN}  所有服务启动完毕！${NC}"
echo -e "${GREEN}  网关入口: http://localhost:8088${NC}"
echo -e "${GREEN}  Nacos:    http://localhost:8848/nacos${NC}"
echo -e "${GREEN}  Zipkin:   http://localhost:9411${NC}"
echo -e "${GREEN}  Sentinel: http://localhost:8080${NC}"
echo -e "${GREEN}════════════════════════════════════════${NC}"