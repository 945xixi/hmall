package com.hmall.common.config;

import com.hmall.common.interceptors.UserInfoInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 这个还要添加到spring.factories才能扫描到【不懂】
@Configuration
@ConditionalOnClass(DispatcherServlet.class) // 此配置类生效的条件。当服务有了DispatcherServlet.class，即有SpringMvc才生效，所以不会在网关中生成，
                                            // 因为网关不是springboot，但网关引入了common模块，所以要添加上条件才行，要不然网关找不到会报错
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInfoInterceptor());
    }
}
