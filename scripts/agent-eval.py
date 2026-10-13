#!/usr/bin/env python3
"""
SmartMall Agent 端到端评测脚本
用法：python3 scripts/agent-eval.py [--base http://127.0.0.1:8092]
评测维度：给定自然语言问题，Agent 是否给出符合预期的结果（工具编排正确）。
"""
import json
import sys
import time
import urllib.request

BASE = "http://127.0.0.1:8092"

# 测试用例：(问题, 预期关键词列表, 描述)
CASES = [
    ("你好，请简单介绍一下你自己", ["助手", "小龙", "购物"], "自我介绍"),
    ("帮我找一款手机", ["iPhone", "手机", "价格"], "商品搜索"),
    ("帮我查一下 iPhone 17 Pro 的库存", ["库存", "件", "台"], "库存查询"),
    ("我要买 1 台 iPhone 17 Pro，我的用户ID是 1，帮我下单", ["预览", "确认", "金额"], "下单预览护栏"),
    ("帮我看看购物车", ["购物车"], "购物车查询"),
    ("查一下订单 22 的信息", ["订单", "待支付", "金额", "状态"], "订单查询"),
]


def chat(session_id, message):
    req = urllib.request.Request(
        BASE + "/api/agent/chat",
        data=json.dumps({"sessionId": session_id, "message": message}).encode(),
        headers={"Content-Type": "application/json"},
        method="POST",
    )
    with urllib.request.urlopen(req, timeout=120) as r:
        return json.loads(r.read().decode())


def main():
    global BASE
    base = sys.argv[1] if len(sys.argv) > 1 else BASE
    BASE = base

    passed = 0
    total = len(CASES)
    print(f"=== SmartMall Agent 评测（{total} 用例）===\n")
    for i, (q, keywords, desc) in enumerate(CASES, 1):
        try:
            resp = chat(f"eval-{i}", q)
            text = resp.get("data") or ""
            ok = any(k in text for k in keywords)
            if ok:
                passed += 1
            print(f"[{'PASS' if ok else 'FAIL'}] 用例{i} [{desc}]")
            print(f"    问题: {q}")
            print(f"    回答: {text[:90].replace(chr(10),' ')}...")
        except Exception as e:
            print(f"[ERROR] 用例{i} [{desc}]: {e}")
        print()

    rate = passed / total * 100
    print(f"=== 结果：{passed}/{total} 通过，正确率 {rate:.0f}% ===")
    return 0 if passed == total else 1


if __name__ == "__main__":
    sys.exit(main())
