package com.smartmall.agent.config;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import com.smartmall.agent.agent.ShopAssistant;
import com.smartmall.agent.tool.ShopTools;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class LangChain4jConfig {

    @Value("${langchain4j.open-ai.api-key}")
    private String apiKey;

    @Value("${langchain4j.open-ai.base-url:https://api.deepseek.com/v1}")
    private String baseUrl;

    @Value("${langchain4j.open-ai.model-name:deepseek-flash}")
    private String modelName;

    @Value("${langchain4j.open-ai.temperature:0.3}")
    private Double temperature;

    @Value("${langchain4j.open-ai.timeout:120s}")
    private Duration timeout;

    /**
     * LLM 对话模型（DeepSeek 走 OpenAI 兼容协议）
     */
    @Bean
    public ChatModel chatModel() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .temperature(temperature)
                .timeout(timeout)
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    /**
     * 智能导购助手（AiServices 自动完成 ReAct：思考→调用工具→观察→回答）
     * 每个 sessionId 维护独立的对话记忆窗口。
     */
    @Bean
    public ShopAssistant shopAssistant(ChatModel chatModel, ShopTools shopTools) {
        Map<String, ChatMemory> memoryStore = new ConcurrentHashMap<>();
        return AiServices.builder(ShopAssistant.class)
                .chatModel(chatModel)
                .tools(shopTools)
                .chatMemoryProvider(sessionId -> memoryStore.computeIfAbsent(
                        String.valueOf(sessionId),
                        id -> MessageWindowChatMemory.withMaxMessages(20)))
                .build();
    }
}
