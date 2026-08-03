package com.smartmall.gateway.filter;

import com.smartmall.gateway.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtUtils jwtUtils;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/user/register",
            "/api/payment/callback",
            "/api/search"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "未登录，请先获取Token");
        }

        try {
            String token = authHeader.substring(7);
            Long userId = jwtUtils.getUserId(token);

            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .build();
            return chain.filter(exchange.mutate().request(request).build());

        } catch (Exception e) {
            return unauthorized(exchange, "Token无效或已过期");
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.contains(path);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"code\":401,\"message\":\"" + message + "\"}";
        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}