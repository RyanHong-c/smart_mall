#!/bin/bash
# ============================================
# SmartMall Agent 一键启动脚本
# 启动顺序：Docker 基础设施 → 微服务 → agent(含前端)
# ============================================

set -e
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$PROJECT_DIR/logs"
mkdir -p "$LOG_DIR"

GREEN='\033[0;32m'; RED='\033[0;31m'; YELLOW='\033[1;33m'; NC='\033[0m'
info() { echo -e "${GREEN}[✓]${NC} $1"; }
warn() { echo -e "${YELLOW}[!]${NC} $1"; }
err()  { echo -e "${RED}[✗]${NC} $1"; }

is_up() { lsof -iTCP:"$1" -sTCP:LISTEN -P -n >/dev/null 2>&1; }
port_of() {
  case "$1" in
    product) echo 8083 ;;
    search) echo 8090 ;;
    inventory) echo 8085 ;;
    cart) echo 8086 ;;
    order) echo 8087 ;;
    agent) echo 8092 ;;
  esac
}

# ---------- 前置检查 ----------
if ! command -v docker >/dev/null 2>&1; then err "未安装 docker"; exit 1; fi
if [ -z "$DEEPSEEK_API_KEY" ]; then
  warn "未设置 DEEPSEEK_API_KEY 环境变量，agent 将无法调用 LLM"
  warn "请先执行：export DEEPSEEK_API_KEY=sk-xxxx"
fi

# ---------- 1. 基础设施 ----------
echo -e "\n==> 1/3 启动 Docker 基础设施"
INFRA=(mysql redis nacos rocketmq-namesrv rocketmq-broker seata-server sentinel-dashboard zipkin elasticsearch)
for c in "${INFRA[@]}"; do
  if docker ps --format '{{.Names}}' | grep -q "^${c}$"; then
    info "$c 已在运行"
  elif docker start "$c" >/dev/null 2>&1; then
    info "$c 已启动"
  else
    err "$c 启动失败（容器不存在？请先 docker create/run 创建）"
  fi
done
info "等待基础设施就绪(20s)..."
sleep 20

# ---------- 2. 微服务 ----------
echo -e "\n==> 2/3 启动微服务"
cd "$PROJECT_DIR"
SERVICES=(product search inventory cart order)
for s in "${SERVICES[@]}"; do
  port=$(port_of "$s")
  if is_up "$port"; then
    info "smartmall-$s 已在运行 (端口 $port)"
  else
    nohup mvn -pl "smartmall-$s" spring-boot:run -q > "$LOG_DIR/$s.log" 2>&1 &
    echo "  → smartmall-$s 启动中 (端口 $port，日志 logs/$s.log)"
  fi
done

# ---------- 3. agent ----------
echo -e "\n==> 3/3 启动 agent + 前端"
if is_up 8092; then
  info "agent 已在运行 (端口 8092)"
else
  nohup mvn -pl smartmall-agent spring-boot:run -q > "$LOG_DIR/agent.log" 2>&1 &
  echo "  → agent 启动中 (端口 8092，日志 logs/agent.log)"
fi

# ---------- 汇总 ----------
echo -e "\n${GREEN}════════════════════════════════════════${NC}"
echo -e "${GREEN}  启动完成！${NC}"
echo "  前端 + Agent : http://localhost:8092"
echo "  Nacos        : http://localhost:8848/nacos"
echo "  ES           : http://localhost:9200"
echo -e "${GREEN}════════════════════════════════════════${NC}"
echo "提示：微服务首次启动需编译，约 30~60 秒后可通过以下命令查看状态："
echo "  for p in 8083 8085 8086 8087 8090 8092; do lsof -iTCP:\$p -sTCP:LISTEN -P -n >/dev/null 2>&1 && echo \"port \$p UP\" || echo \"port \$p down\"; done"
