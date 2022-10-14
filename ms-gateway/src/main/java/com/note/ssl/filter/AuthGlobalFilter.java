package com.note.ssl.filter;

import com.note.ssl.component.HandleException;
import com.note.ssl.config.IgnoreUrlsConfig;
import com.note.ssl.config.RestTemplateConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author SongShengLin
 * @date 2022/10/13 21:49
 * @description 全局网关过滤器
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    /**
     * 白名单配置
     */
    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HandleException handleException;

    /**
     * 身份校验处理，除了白名单配置方形，其余都过滤掉
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // spring带的URL路径检查工具
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean flag = false;
        // 获取请求路径
        String path = exchange.getRequest().getURI().getPath();
        for (String url : ignoreUrlsConfig.getUrls()) {
            if (antPathMatcher.match(url, path)) {
                flag = true;
                break;
            }
        }
        // 如果匹配上配置的白名单，就放行
        if (flag) {
            return chain.filter(exchange);
        }
        // 如果没有匹配上白名单，就校验access_token
        String accessToken = exchange.getRequest().getQueryParams().getFirst("access_token");
        if (StringUtils.isBlank(accessToken)) {
            return handleException.writeError(exchange, "请登录！");
        }
        // 发送远程请求，判断token是否有效
        String checkTokenUrl = "http://ms-oauth2-server/oauth/check_token?token=".concat(accessToken);
        try {
            ResponseEntity<String> entity = restTemplate.getForEntity(checkTokenUrl, String.class);
            if (entity.getStatusCode() != HttpStatus.OK) {
                return handleException.writeError(exchange, "Token was not recognised, token:".concat(accessToken));
            }
            if (StringUtils.isBlank(entity.getBody())) {
                return handleException.writeError(exchange, "This token is invalid:".concat(accessToken));
            }
        } catch (Exception e) {
            return handleException.writeError(exchange, "Token was not recognised, token:".concat(e.getMessage()));
        }
        return chain.filter(exchange);
    }

    /**
     * 网关过滤器的排序，数字越小优先级越高
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
