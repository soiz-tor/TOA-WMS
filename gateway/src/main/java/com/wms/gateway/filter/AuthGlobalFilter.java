package com.wms.gateway.filter;

import jakarta.annotation.Resource;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 三级网关，防请求重放攻击
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    private static final String REQUEST_TIMESTAMP_HEADER = "timestamp";

    private static final String X_SIGN = "X-Sign";

    private static final long TIME_TOLERANCE = 60000;

    private static final String SECRET_KEY = "SECRET";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    Logger logger = LoggerFactory.getLogger(AuthGlobalFilter.class);

    private final List<String> ignoredPaths = Arrays.asList("/sign_in", "/sign_up");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            String path = exchange.getRequest().getPath().value();
            if (ignoredPaths.contains(path)) {
                return chain.filter(exchange);
            }
            //前端签名后的值
            String signature = exchange.getRequest().getHeaders().getFirst(X_SIGN);
            //前端传递的时间戳
            String timestamp = exchange.getRequest().getHeaders().getFirst(REQUEST_TIMESTAMP_HEADER);
            //前端传递的noncestr
            String nonceStr = exchange.getRequest().getHeaders().getFirst(REQUEST_ID_HEADER);
            //MD5加密一遍
            String encodeRequestHeader = DigestUtils.md5Hex(timestamp+nonceStr+SECRET_KEY);
            if (nonceStr == null || nonceStr.isBlank() || timestamp == null || timestamp.isBlank() || signature == null || signature.isBlank()) {
                logger.info("header为空");
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            } else if (System.currentTimeMillis() - Long.parseLong(timestamp) > TIME_TOLERANCE) {
                logger.info("大于10秒");
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            } else if (redisTemplate.opsForValue().get(exchange.getRequest().getURI().getPath()) != null) {
                logger.info("重复请求key");
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            } else if (!Objects.equals(signature, encodeRequestHeader)) {
                logger.info("非法key");
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
            redisTemplate.opsForValue().set(exchange.getRequest().getURI().getPath(), nonceStr, TIME_TOLERANCE, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 3;
    }
}
