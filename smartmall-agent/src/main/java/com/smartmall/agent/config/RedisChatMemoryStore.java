package com.smartmall.agent.config;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.JacksonChatMessageJsonCodec;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * Redis 持久化的对话记忆存储。
 *
 * 把 LangChain4j 的 ChatMessage 序列化为 JSON 存入 Redis，
 * 使多轮对话记忆在服务重启后仍然保留，并支持多实例共享。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisChatMemoryStore implements ChatMemoryStore {

    private final RedisTemplate<String, String> redisTemplate;
    private final JacksonChatMessageJsonCodec codec = new JacksonChatMessageJsonCodec();

    private static final String KEY_PREFIX = "agent:memory:";
    private static final long TTL_SECONDS = 60 * 60 * 24; // 24 小时

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        try {
            String json = redisTemplate.opsForValue().get(KEY_PREFIX + memoryId);
            if (json == null) {
                return List.of();
            }
            return codec.messagesFromJson(json);
        } catch (Exception e) {
            log.warn("读取对话记忆失败，memoryId={}, err={}", memoryId, e.getMessage());
            return List.of();
        }
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        try {
            redisTemplate.opsForValue().set(
                    KEY_PREFIX + memoryId,
                    codec.messagesToJson(messages),
                    Duration.ofSeconds(TTL_SECONDS));
        } catch (Exception e) {
            log.warn("写入对话记忆失败，memoryId={}, err={}", memoryId, e.getMessage());
        }
    }

    @Override
    public void deleteMessages(Object memoryId) {
        try {
            redisTemplate.delete(KEY_PREFIX + memoryId);
        } catch (Exception e) {
            log.warn("删除对话记忆失败，memoryId={}, err={}", memoryId, e.getMessage());
        }
    }
}
