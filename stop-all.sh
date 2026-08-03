#!/bin/bash
# ============================================
# SmartMall 一键停止脚本
# ============================================

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

log_info() { echo -e "${GREEN}[INFO]${NC}  $1"; }

echo "============================================"
echo "  停止 SmartMall 所有服务"
echo "============================================"

# 停止微服务
PORTS=(8081 8082 8083 8084 8085 8086 8087 8088)
for port in "${PORTS[@]}"; do
    PID=$(lsof -tiTCP:"$port" -sTCP:LISTEN 2>/dev/null)
    if [ -n "$PID" ]; then
        kill "$PID" 2>/dev/null && log_info "已停止端口 $port 的服务 (PID=$PID)"
    else
        log_info "端口 $port 无服务运行"
    fi
done

echo ""
log_info "微服务已全部停止"
echo ""
echo "Docker 容器仍然运行，如需停止请执行:"
echo "  docker stop mysql redis nacos rocketmq-namesrv rocketmq-broker sentinel-dashboard seata-server zipkin"