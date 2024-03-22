package com.hauth.cas.oauth2.impl;

import com.hauth.cas.oauth2.TokenGenerator;
import com.hauth.cas.oauth2.TokenRequestHandler;
import com.hauth.cas.oauth2.TokenStorage;
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
    private TokenGenerator tokenGenerator;

    @Autowired
    private TokenStorage tokenStorage;

    protected AccessToken generateToken(UserProfile userProfile) {
        String accessToken = tokenGenerator.generateAccessToken(userProfile);
        String refreshToken = tokenGenerator.generateRefreshToken(userProfile);
        tokenStorage.saveAccessToken(accessToken, userProfile);
        tokenStorage.saveRefreshToken(refreshToken, userProfile);

        return AccessToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(TokenGenerator.BEARER)
                .expiresIn(TokenGenerator.EXPIRES_IN)
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
