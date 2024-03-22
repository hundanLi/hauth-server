package com.hauth.cas.oauth2.impl;

import com.hauth.cas.oauth2.config.OAuth20GrantType;
import com.hauth.cas.oauth2.dto.AccessTokenRequest;
import com.hauth.cas.oauth2.dto.UserProfile;
import org.springframework.stereotype.Service;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/22 11:23
 */
@Service
public class ClientCredentialTokenRequestHandler extends AbstractTokenRequestHandler{
    @Override
    public boolean supports(String grantType) {
        return OAuth20GrantType.GRANT_TYPE_CLIENT_CREDENTIALS.equals(grantType);
    }

    @Override
    protected UserProfile validateRequest(AccessTokenRequest accessTokenRequest) {
        return UserProfile.builder()
                .clientId(accessTokenRequest.getClientId())
                .principal(accessTokenRequest.getClientId())
                .id(accessTokenRequest.getClientId())
                .build();
    }

}
