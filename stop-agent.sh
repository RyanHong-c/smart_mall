#!/bin/bash
# 停止 SmartMall Agent 相关微服务（不停止 Docker 基础设施）
PORTS=(8083 8085 8086 8087 8090 8092)
for p in "${PORTS[@]}"; do
  PID=$(lsof -tiTCP:"$p" -sTCP:LISTEN 2>/dev/null)
  if [ -n "$PID" ]; then
    kill "$PID" 2>/dev/null && echo "已停止端口 $p 的服务 (PID=$PID)"
  else
    echo "端口 $p 无服务运行"
  fi
done

# 如需同时停止 Docker 基础设施容器，执行：docker compose stop
