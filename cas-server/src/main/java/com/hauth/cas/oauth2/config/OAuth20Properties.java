package com.hauth.cas.oauth2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/19 23:28
 */
@Data
@Component
@ConfigurationProperties(prefix = "oauth")
public class OAuth20Properties {

    /**
     * 认证服务器地址
     */
    private String authServer;

    /**
     * oauth授权服务地址
     */
    private String oauthServer;

    private String authorizeEndpoint = "/oauth2.0/authorize";

    private String authLoginUri = "/cas/login";
    private String authValidateUri = "/cas/p3/serviceValidate";

    private String tokenSecret;
    private Duration tokenTimeout = Duration.ofDays(3);
    private Duration refreshTimeout = Duration.ofDays(30);
}
