package com.hmall.api.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;


public class DefaultFeignConfig {
    // TODO：抽取Feign Client公共模块，减少重复代码编写？
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
