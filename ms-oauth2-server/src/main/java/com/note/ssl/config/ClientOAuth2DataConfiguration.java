package com.note.ssl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: SongShengLin
 * @Date: 2022/10/11 15:50
 * @Describe: 配置文件中获取客户端权限对象
 */
@Data
@Component
@ConfigurationProperties(prefix = "client.oauth2")
public class ClientOAuth2DataConfiguration {

    // 客户端标识 ID
    private String clientId;

    // 客户端安全码
    private String secret;

    // 授权类型
    private String[] grantTypes;

    // token有效期
    private int tokenValidityTime;

    // refresh-token有效期
    private int refreshTokenValidityTime;

    // 客户端访问范围
    private String[] scopes;
}
