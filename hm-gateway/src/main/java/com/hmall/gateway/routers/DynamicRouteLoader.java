package com.hmall.gateway.routers;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicRouteLoader {

    // 获取配置和监听器
    private final NacosConfigManager nacosConfigManager;
    // 修改路由表
    private final RouteDefinitionWriter writer;
    // 存放routeIds用于删除
    private final Set<String> routeIds = new HashSet<>();

    // 底层是解析json的，所以把yaml的改为json
    private final String dataId = "gateway-routes.json";

    private final String group = "DEFAULT_GROUP";

    // TODO：服务配置：动态路由
    @PostConstruct // 在项目构造后加载
    public void initRouteConfigListener() throws NacosException {
        // 1、项目启动时，先拉取一次配置，并添加到配置监听器
        String configInfo = nacosConfigManager.getConfigService()
                .getConfigAndSignListener(dataId, group, 5000, new Listener() {
                    // 线程池
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        // 3、监听到配置改变，然后更新路由表
                        updateConfigInfo(configInfo);
                    }
                });

        // 2、第一次读取配置，更新到网关的路由表中
        updateConfigInfo(configInfo);
    }

    public void updateConfigInfo(String configInfo) {
        log.info("路由信息：{}", configInfo);
        // 解析配置信息，转为RouteDefinition
        List<RouteDefinition> routeDefinitions = JSONUtil.toList(configInfo, RouteDefinition.class);

        // 删除旧的路由表（因为如果不删直接更新，则如果是不要的路由就不能处理，所以直接全删）
        // 容器创建就在spring中存在了，且是同一个，所以不管哪次使用这个容器的routeIds，routeIds都是我们需要的
        for (String routeId : routeIds) {
            writer.delete(Mono.just(routeId)).subscribe();
        }

        routeIds.clear();

        // 更新路由表
        for (RouteDefinition routeDefinition : routeDefinitions) {
            // 更新路由表
            writer.save(Mono.just(routeDefinition)).subscribe();
            // 记录id，便于下一次更新时删除
            routeIds.add(routeDefinition.getId());
        }
    }

}
