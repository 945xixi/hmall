package com.hmall.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// TODO：配置热更新
@Configuration
@Data
@ConfigurationProperties(prefix = "hm.cart")
public class CartProperties {
    private Integer maxItems;
}
