package com.wms.gateway.filter;


import com.wms.api.util.JWTUtil;
import com.wms.api.entity.User;
import com.wms.gateway.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * 一级网关，判断token
 */
@Component
public class TokenFilter implements GlobalFilter, Ordered {

    private static final String AUTHORIZATION = "Authorization";
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private UserMapper userMapper;

    private final List<String> ignoredPaths = Arrays.asList("/api/sign_in", "/api/sign_up");

    Logger logger = LoggerFactory.getLogger(TokenFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            String path = exchange.getRequest().getPath().value();
            if (ignoredPaths.contains(path)) {
                return chain.filter(exchange);
            }
            String token = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION);
            if (token == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            User user = userMapper.findUserByUsername(JWTUtil.getUser(token));
            if (user == null || !JWTUtil.verify(token, user.username(), user.password())) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            } else if (Boolean.TRUE.equals(redisTemplate.hasKey(token))) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
