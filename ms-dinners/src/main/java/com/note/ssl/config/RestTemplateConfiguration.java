package com.note.ssl.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Rest 配置类
 * 远程调用类
 */
@Configuration
public class RestTemplateConfiguration {

    @LoadBalanced// 负载均衡 高可用
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}