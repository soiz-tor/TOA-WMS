package com.wms.gateway.filter;

import com.wms.api.entity.User;
import com.wms.api.util.JWTUtil;
import com.wms.gateway.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * 二级网关，判断权限
 */
@Component
public class PermissionFilter implements GlobalFilter, Ordered {

    Logger logger = LoggerFactory.getLogger(PermissionFilter.class);

    @Resource
    private UserMapper userMapper;

    private static final String AUTHORIZATION = "Authorization";

    private final List<String> ignoredPaths = Arrays.asList("/api/sign_in", "/api/user/profile", "/api/change_password", "/api/logout");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            String path = exchange.getRequest().getPath().value();
            if (ignoredPaths.contains(path)) {
                return chain.filter(exchange);
            }
            String token = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION);
            User user = userMapper.findUserByUsername(JWTUtil.getUser(token));
            if (user.permission() != 0) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
