package com.hauth.cas.oauth2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/18 19:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenRequest {

    private String grantType;
    private String clientId;
    private String clientSecret;
    private String code;
    private String redirectUri;
    private String codeVerifier;
    private String username;
    private String password;
    private String refreshToken;
}
