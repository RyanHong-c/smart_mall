package com.smartmall.agent.controller;

import com.smartmall.agent.agent.ShopAssistant;
import com.smartmall.agent.dto.ChatRequest;
import com.smartmall.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final ShopAssistant shopAssistant;

    /**
     * 智能导购对话接口
     *
     * POST /api/agent/chat
     * body: {"sessionId": "可选", "message": "帮我找一款 5000 元以内的手机"}
     */
    @PostMapping("/chat")
    public Result<String> chat(@RequestBody @Valid ChatRequest req) {
        String sessionId = req.getSessionId() != null && !req.getSessionId().isBlank()
                ? req.getSessionId()
                : UUID.randomUUID().toString();
        log.info("agent chat, sessionId={}, message={}", sessionId, req.getMessage());
        String reply = shopAssistant.chat(sessionId, req.getMessage());
        return Result.success(reply);
    }
}
