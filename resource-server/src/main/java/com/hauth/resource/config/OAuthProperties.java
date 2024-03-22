package com.hauth.resource.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/22 0:09
 */
@Data
@Component
@ConfigurationProperties(prefix = "oauth")
public class OAuthProperties {

    private String server;
    private String clientId;
    private String clientSecret;
    private String redirectUri;

    private String authName = "AUTHENTICATION";
    private String stateName = "state";
    private String codeChallengeName = "code_challenge";
    private String codeVerifierName = "code_verifier";

}
