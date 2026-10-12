package com.smartmall.agent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {

    /** 会话 ID，用于多轮对话记忆隔离；不传时由服务端自动生成 */
    private String sessionId;

    @NotBlank(message = "消息不能为空")
    private String message;
}
