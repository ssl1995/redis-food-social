package com.note.ssl.config;

import com.note.ssl.commons.model.domain.SignInIdentity;
import com.note.ssl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: SongShengLin
 * @Date: 2022/10/11 15:49
 * @Describe: 授权配置类
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private ClientOAuth2DataConfiguration clientOAuth2DataConfiguration;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTokenStore redisTokenStore;

    /**
     * 配置令牌端点安全约束
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        // 将SecurityConfiguration配置的antMatchersURL给放行了
        security.tokenKeyAccess("permitAll()")// oauth/token_key放行
                .checkTokenAccess("permitAll()");// oauth/check_token放行
    }

    /**
     * 客户端配置- 授权类型
     * 授权模型在配置文件中设置了
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // 从配置文件中读取自定义的配置
        clients.inMemory()
                .withClient(clientOAuth2DataConfiguration.getClientId())
                .secret(passwordEncoder.encode(clientOAuth2DataConfiguration.getSecret()))
                .authorizedGrantTypes(clientOAuth2DataConfiguration.getGrantTypes())
                .accessTokenValiditySeconds(clientOAuth2DataConfiguration.getTokenValidityTime())// token有效期
                .refreshTokenValiditySeconds(clientOAuth2DataConfiguration.getRefreshTokenValidityTime())
                .scopes(clientOAuth2DataConfiguration.getScopes());
    }

    /**
     * 配置授权以及令牌的访问端点和令牌服务
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // endpoints:认证器
        endpoints.authenticationManager(authenticationManager)
                // 具体登录的方法
                .userDetailsService(userService)
                // token存储的方式
                .tokenStore(redisTokenStore)
                .tokenEnhancer((accessToken, authentication) -> {// 令牌登录增强
                    SignInIdentity signInIdentity = (SignInIdentity) authentication.getPrincipal();
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("nickname", signInIdentity.getNickname());
                    map.put("avatarUrl", signInIdentity.getAvatarUrl());
                    DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
                    token.setAdditionalInformation(map);
                    return token;
                })
        ;
    }
}
