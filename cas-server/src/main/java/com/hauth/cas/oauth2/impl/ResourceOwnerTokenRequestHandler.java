package com.hauth.cas.oauth2.impl;

import com.hauth.cas.auth.Authentication;
import com.hauth.cas.auth.AuthenticationManager;
import com.hauth.cas.auth.impl.UserPasswordAuthentication;
import com.hauth.cas.oauth2.config.OAuth20GrantType;
import com.hauth.cas.oauth2.dto.AccessTokenRequest;
import com.hauth.cas.oauth2.dto.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/21 16:24
 */
@Slf4j
@Service
public class ResourceOwnerTokenRequestHandler extends AbstractTokenRequestHandler{

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public boolean supports(String grantType) {
        return OAuth20GrantType.GRANT_TYPE_RESOURCE_OWNER.equals(grantType);
    }

    @Override
    protected UserProfile validateRequest(AccessTokenRequest accessTokenRequest) {
        String username = accessTokenRequest.getUsername();
        String password = accessTokenRequest.getPassword();
        UserPasswordAuthentication userPasswordAuthentication = new UserPasswordAuthentication(username, password);
        Authentication authentication = authenticationManager.authenticate(userPasswordAuthentication);
        if (!authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Invalid credentials for user: " + username);
        }
        return UserProfile.builder()
                .id(username)
                .principal(username)
                .attributes(authentication.getAttributes())
                .build();
    }
}
