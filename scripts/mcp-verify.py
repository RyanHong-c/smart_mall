#!/usr/bin/env python3
"""
MCP Server 验证脚本：通过 stdio 与 agent 的 MCP server 握手，检查暴露的工具。

用法：
  1. 打包：mvn -pl smartmall-agent -am package -DskipTests
  2. 运行本脚本（内部自动 java -jar 启动 MCP 模式，需先 export DEEPSEEK_API_KEY）
     python3 scripts/mcp-verify.py

预期输出：initialize 握手成功 + 列出 8 个 @Tool 工具。
"""
import json
import os
import select
import subprocess
import sys
import time

JAR = "smartmall-agent/target/smartmall-agent-0.0.1-SNAPSHOT-exec.jar"


def main():
    if not os.path.exists(JAR):
        print(f"未找到 {JAR}，请先执行 mvn -pl smartmall-agent -am package -DskipTests")
        sys.exit(1)

    env = os.environ.copy()
    env["MCP_ENABLED"] = "true"

    proc = subprocess.Popen(
        ["java", "-jar", JAR],
        stdin=subprocess.PIPE, stdout=subprocess.PIPE,
        stderr=open("/tmp/mcp-stderr.log", "w"),
        env=env, text=True, bufsize=1,
    )

    time.sleep(18)

    def send(obj):
        proc.stdin.write(json.dumps(obj) + "\n")
        proc.stdin.flush()

    def read_json(secs=4):
        out, end = [], time.time() + secs
        while time.time() < end:
            r, _, _ = select.select([proc.stdout], [], [], 0.2)
            if r:
                line = proc.stdout.readline().strip()
                if line.startswith("{"):
                    try:
                        out.append(json.loads(line))
                    except Exception:
                        pass
        return out

    # 1. initialize
    send({"jsonrpc": "2.0", "id": 1, "method": "initialize",
          "params": {"protocolVersion": "2025-03-26", "capabilities": {},
                     "clientInfo": {"name": "eval", "version": "1.0"}}})
    for m in read_json():
        if m.get("id") == 1:
            print("✓ initialize 成功:", m["result"].get("serverInfo"), "capabilities:", m["result"].get("capabilities"))

    # 2. tools/list
    send({"jsonrpc": "2.0", "id": 2, "method": "tools/list"})
    for m in read_json():
        if m.get("id") == 2:
            tools = m["result"]["tools"]
            print(f"✓ tools/list 返回 {len(tools)} 个工具：")
            for t in tools:
                print(f"   - {t['name']}: {t.get('description', '')[:50]}")

    proc.terminate()


if __name__ == "__main__":
    main()
