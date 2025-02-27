package com.cgesgin.todo_list_api.config.rate_limit;

import io.github.bucket4j.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final ConcurrentMap<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket getBucket(String ip) {
        return cache.computeIfAbsent(ip, k -> Bucket.builder()
                .addLimit(Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1))))
                .build());
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String ip = request.getRemoteAddr();
        Bucket bucket = getBucket(ip);
    
        if (bucket.tryConsume(1)) {
            response.setHeader("X-RateLimit-Status", "normal");
            return true;
        } else {
            try {
                Thread.sleep(2000); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            response.setHeader("X-RateLimit-Status", "throttled");
            response.getWriter().write("{\"error\": \"Too Many Requests\", \"message\": \"Rate limit exceeded\"}");
            return false;
        }
    }
    
    
}
