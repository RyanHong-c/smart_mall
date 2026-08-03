package com.smartmall.utils;

import brave.Span;
import brave.Tracing;
import brave.propagation.TraceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TraceContextPropagator {

    private final Tracing tracing;

    public TraceContextInfo capture() {
        Span span = tracing.tracer().currentSpan();
        if (span == null) {
            return null;
        }
        TraceContext ctx = span.context();
        return new TraceContextInfo(ctx.traceIdString(), ctx.spanIdString());
    }

    public Span startChildSpan(TraceContextInfo info, String name) {
        if (info == null) {
            return tracing.tracer().nextSpan().name(name).start();
        }
        long traceIdHigh = 0;
        long traceIdLow;
        String traceIdStr = info.traceId();
        if (traceIdStr.length() > 16) {
            traceIdHigh = parseHex(traceIdStr.substring(0, traceIdStr.length() - 16));
            traceIdLow = parseHex(traceIdStr.substring(traceIdStr.length() - 16));
        } else {
            traceIdLow = parseHex(traceIdStr);
        }
        TraceContext parent = TraceContext.newBuilder()
                .traceIdHigh(traceIdHigh)
                .traceId(traceIdLow)
                .spanId(parseHex(info.spanId()))
                .build();
        return tracing.tracer().newChild(parent).name(name).start();
    }

    private long parseHex(String hex) {
        return Long.parseUnsignedLong(hex, 16);
    }

    public record TraceContextInfo(String traceId, String spanId) {}
}