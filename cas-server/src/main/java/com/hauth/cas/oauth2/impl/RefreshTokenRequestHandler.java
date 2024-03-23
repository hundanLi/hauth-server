package com.hauth.cas.oauth2.impl;

import com.hauth.cas.oauth2.TokenManager;
import com.hauth.cas.oauth2.TokenStorage;
import com.hauth.cas.oauth2.config.OAuth20GrantType;
import com.hauth.cas.oauth2.dto.AccessTokenRequest;
import com.hauth.cas.oauth2.dto.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/23 14:45
 */
@Slf4j
@Service
public class RefreshTokenRequestHandler extends AbstractTokenRequestHandler {

    @Autowired
    private TokenStorage tokenStorage;
    @Autowired
    private TokenManager tokenManager;

    @Override
    public boolean supports(String grantType) {
        return OAuth20GrantType.GRANT_TYPE_REFRESH_TOKEN.equals(grantType);
    }

    @Override
    protected UserProfile validateRequest(AccessTokenRequest accessTokenRequest) {
        String refreshToken = accessTokenRequest.getRefreshToken();
        UserProfile userProfile = tokenManager.validateRefreshToken(refreshToken);
        if (userProfile == null) {
            throw new IllegalArgumentException("Invalid refresh token: " + refreshToken);
        }
        tokenStorage.removeRefreshToken(refreshToken);
        return userProfile;

    }
}
