package com.smartmall.agent.agent;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * 智能导购助手接口
 *
 * 由 LangChain4j 的 AiServices 在运行时生成代理实现：
 * 大模型会自动决定是否调用工具、调用哪个工具、传什么参数。
 */
public interface ShopAssistant {

    @SystemMessage(Prompts.SHOP_SYSTEM_PROMPT)
    String chat(@MemoryId String sessionId, @UserMessage String message);
}
