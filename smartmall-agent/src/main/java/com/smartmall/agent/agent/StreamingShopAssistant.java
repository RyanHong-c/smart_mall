package com.smartmall.agent.agent;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

/**
 * 流式智能导购助手接口（返回 TokenStream，支持 SSE 逐字输出）
 */
public interface StreamingShopAssistant {

    @SystemMessage(Prompts.SHOP_SYSTEM_PROMPT)
    TokenStream chat(@MemoryId String sessionId, @UserMessage String message);
}
