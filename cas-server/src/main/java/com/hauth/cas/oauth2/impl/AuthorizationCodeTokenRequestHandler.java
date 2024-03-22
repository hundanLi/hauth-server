package com.hauth.cas.oauth2.impl;

import com.hauth.cas.oauth2.AuthorizationConsentManager;
import com.hauth.cas.oauth2.config.OAuth20GrantType;
import com.hauth.cas.oauth2.dto.AccessTokenRequest;
import com.hauth.cas.oauth2.dto.AuthorizeConsent;
import com.hauth.cas.oauth2.dto.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/21 16:43
 */
@Slf4j
@Service
public class AuthorizationCodeTokenRequestHandler extends AbstractTokenRequestHandler {
    @Autowired
    private AuthorizationConsentManager authorizationConsentManager;

    @Override
    public boolean supports(String grantType) {
        return OAuth20GrantType.GRANT_TYPE_AUTHORIZATION_CODE.equals(grantType);
    }

    @Override
    protected UserProfile validateRequest(AccessTokenRequest accessTokenRequest) {
        AuthorizeConsent consent = authorizationConsentManager.getConsent(accessTokenRequest.getCode());
        if (consent == null) {
            throw new IllegalArgumentException("Invalid code: " + accessTokenRequest.getCode());
        }
        authorizationConsentManager.removeConsent(accessTokenRequest.getCode());
        String user = consent.getUser();
        return UserProfile.builder()
                .id(user)
                .principal(user)
                .attributes(consent.getAttributes())
                .build();
    }
}
