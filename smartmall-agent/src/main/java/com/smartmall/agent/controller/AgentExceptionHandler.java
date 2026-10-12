package com.smartmall.agent.controller;

import com.smartmall.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AgentExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .findFirst()
                .orElse("参数错误");
        return Result.fail(400, msg);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("Agent 服务异常", e);
        String msg = e.getMessage() != null && e.getMessage().contains("401")
                ? "LLM 鉴权失败，请检查 API Key"
                : "智能体处理失败：" + (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        return Result.fail(500, msg);
    }
}
