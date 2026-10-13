package com.smartmall.agent.controller;

import com.smartmall.agent.agent.ShopAssistant;
import com.smartmall.agent.agent.StreamingShopAssistant;
import com.smartmall.agent.dto.ChatRequest;
import com.smartmall.result.Result;
import dev.langchain4j.service.TokenStream;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final ShopAssistant shopAssistant;
    private final StreamingShopAssistant streamingShopAssistant;

    /**
     * 非流式对话
     * POST /api/agent/chat
     */
    @PostMapping("/chat")
    public Result<String> chat(@RequestBody @Valid ChatRequest req) {
        String sessionId = resolveSessionId(req);
        log.info("agent chat, sessionId={}, message={}", sessionId, req.getMessage());
        String reply = shopAssistant.chat(sessionId, req.getMessage());
        return Result.success(reply);
    }

    /**
     * 流式对话（SSE 逐字输出）
     * POST /api/agent/chat/stream
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestBody @Valid ChatRequest req) {
        String sessionId = resolveSessionId(req);
        log.info("agent stream chat, sessionId={}, message={}", sessionId, req.getMessage());

        SseEmitter emitter = new SseEmitter(0L);
        TokenStream tokenStream = streamingShopAssistant.chat(sessionId, req.getMessage());

        tokenStream
                .onPartialResponse(token -> {
                    try {
                        emitter.send(SseEmitter.event().data(token));
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                })
                .onCompleteResponse(response -> emitter.complete())
                .onError(err -> {
                    log.error("流式对话失败", err);
                    try {
                        emitter.send(SseEmitter.event().data("\n\n[错误] " + err.getMessage()));
                    } catch (Exception ignored) {
                    }
                    emitter.complete();
                })
                .start();

        return emitter;
    }

    private String resolveSessionId(ChatRequest req) {
        return req.getSessionId() != null && !req.getSessionId().isBlank()
                ? req.getSessionId()
                : UUID.randomUUID().toString();
    }
}
