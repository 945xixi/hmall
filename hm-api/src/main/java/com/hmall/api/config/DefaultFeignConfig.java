package com.hmall.api.config;

import com.hmall.api.client.ItemClient;
import com.hmall.api.fallback.ItemClientFallback;
import com.hmall.common.utils.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;


public class DefaultFeignConfig {
    // TODO：抽取Feign Client公共模块，减少重复代码编写？
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    // TODO：③openfeign拦截上一个服务发出的请求，在请求头中添加用户信息
    // 可以写在这个配置类中，也可以弄新的配置类写（若如此，要在调用者的启动类中用注解指明）
    @Bean
    public RequestInterceptor userInfoRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                // 获取调用者中的存的用户信息
                Long userId = UserContext.getUser();
                if (userId != null) {
                    // 在请求头中添加用户信息
                    requestTemplate.header("user-info", userId.toString());
                }
            }
        };
    }

    @Bean
    public ItemClientFallback itemClientFallback() {
        return new ItemClientFallback();
    }
}
