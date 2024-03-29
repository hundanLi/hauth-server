package com.hauth.cas.oauth2.impl;

import com.hauth.cas.oauth2.TokenManager;
import com.hauth.cas.oauth2.TokenRequestHandler;
import com.hauth.cas.oauth2.TokenStorage;
import com.hauth.cas.oauth2.config.OAuth20Properties;
import com.hauth.cas.oauth2.dto.AccessToken;
import com.hauth.cas.oauth2.dto.AccessTokenRequest;
import com.hauth.cas.oauth2.dto.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/21 16:33
 */
@Slf4j
public abstract class AbstractTokenRequestHandler implements TokenRequestHandler {

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private TokenStorage tokenStorage;

    @Autowired
    private OAuth20Properties oAuth20Properties;

    protected AccessToken generateToken(UserProfile userProfile) {
        String accessToken = tokenManager.generateAccessToken(userProfile);
        String refreshToken = tokenManager.generateRefreshToken(userProfile);
        tokenStorage.saveAccessToken(accessToken, userProfile);
        tokenStorage.saveRefreshToken(refreshToken, userProfile);

        return AccessToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(TokenManager.BEARER)
                .expiresIn((int) oAuth20Properties.getTokenTimeout().getSeconds())
                .build();
    }

    @Override
    public AccessToken handleRequest(AccessTokenRequest accessTokenRequest) {
        UserProfile userProfile = validateRequest(accessTokenRequest);
        if (userProfile == null) {
            return null;
        } else {
            return generateToken(userProfile);
        }
    }


    /**
     * 校验请求并返回用户信息
     *
     * @param accessTokenRequest token请求
     * @return 用户信息
     */
    protected abstract UserProfile validateRequest(AccessTokenRequest accessTokenRequest);
}
