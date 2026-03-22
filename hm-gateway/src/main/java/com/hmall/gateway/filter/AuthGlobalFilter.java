package com.hmall.gateway.filter;

import com.hmall.gateway.config.AuthProperties;
import com.hmall.gateway.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthProperties.class)
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    // TODO：网关-JWT登录校验、①传递用户信息

    private final AuthProperties authProperties;

    private final JwtTool jwtTool;
    // 	spring提供专门用来匹配请求路径和自定义的路径pattern的
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求
        ServerHttpRequest request = exchange.getRequest();
        // 检验是否是需要放行的路径
        if (isExclude(request.getPath().toString())){
            // 放行
            return chain.filter(exchange);
        }

        // 获取请求头中的token，此项目规定前端使用authorization存放
        String token = null;
        List<String> headers = request.getHeaders().get("authorization");
        if (headers != null && !headers.isEmpty()) {
            // 此项目只会给 Authorization 设置token的值，所以直接获取第一个
            token = headers.get(0);
        }

        Long userId = null;
        // 校验、解析token，看是否正确
        try {
            // 如果不对，会直接抛异常
            userId = jwtTool.parseToken(token);
        }catch (Exception e){
            // 不正确，返回401状态码
            ServerHttpResponse response = exchange.getResponse();
            // 设置401
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            // 直接返回响应
            return response.setComplete();
        }

        // 传递用户信息，放在请求头中
        String userInfo = userId.toString();
        ServerWebExchange swe = exchange.mutate()
                .request(builder -> builder.header("user-info", userInfo)) // build可以修改exchange中的东西
                .build();

        return chain.filter(swe);
    }

    private boolean isExclude(String path) {
        List<String> excludePaths = authProperties.getExcludePaths();
        for (String pathPattern : excludePaths) {
            // 只要匹配到一个，就放行
            if (antPathMatcher.match(pathPattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 数字越小优先级越高。过滤器不止一个，是一条过滤器链。
     * 设置这个是为了让我们写的过滤器先执行
     * 【继承Ordered就是为了这个】
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }


}
