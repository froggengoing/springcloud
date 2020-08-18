package com.froggengo.cloud.config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator  getRouter(RouteLocatorBuilder routeLocatorBuilder){
        RouteLocatorBuilder.Builder routes = routeLocatorBuilder.routes();
        RouteLocator gateBaidu = routes.route("gateBaidu", r -> r.path("/guonei").uri("http://news.baidu.com/guonei")).build();
        return gateBaidu;
    }
    @Bean
    public RouteLocator  getGuojiRouter(RouteLocatorBuilder routeLocatorBuilder){
        RouteLocatorBuilder.Builder routes = routeLocatorBuilder.routes();
        RouteLocator gateBaidu = routes.route("gateBaidu", r -> r.path("/guoji").uri("http://news.baidu.com/guoji")).build();
        return gateBaidu;
    }

}
