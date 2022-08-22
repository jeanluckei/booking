package com.alten.booking.api.config;

import com.alten.booking.business.exception.TooManyRequestsException;
import com.alten.booking.business.service.RateLimiter;
import io.github.bucket4j.ConsumptionProbe;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
@AllArgsConstructor
public class RateLimitingFilter implements WebFilter, Ordered {

    private RateLimiter rateLimiter;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String username = request.getHeaders().getFirst("username");
        ConsumptionProbe consumptionProbe =
                rateLimiter.resolveBucket(Objects.nonNull(username) ? username : "GUEST")
                        .tryConsumeAndReturnRemaining(1);
        Long remainingLimit = consumptionProbe.getRemainingTokens();
        if (!consumptionProbe.isConsumed()) {
            response.getHeaders().set("X-Rate-Limit-Retry-After-Seconds",
                    String.valueOf(consumptionProbe.getNanosToWaitForRefill() / 1_000_000_000));
            return Mono.error(new TooManyRequestsException("TOO MANY REQUEST"));
        }
        response.getHeaders().set("X-Rate-Limit-Remaining", String.valueOf(remainingLimit));
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}