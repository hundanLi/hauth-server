package com.hauth.cas.oauth2.impl;

import com.hauth.cas.oauth2.TokenGenerator;
import com.hauth.cas.oauth2.dto.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/20 15:10
 */
@Slf4j
@Service
public class SimpleTokenGenerator implements TokenGenerator {


    @Override
    public String generateAccessToken(UserProfile userProfile) {
        return TokenGenerator.AT_PREFIX + Base64.getUrlEncoder().encodeToString(userProfile.getPrincipal().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateRefreshToken(UserProfile userProfile) {
        return TokenGenerator.RT_PREFIX + Base64.getUrlEncoder().encodeToString(userProfile.getPrincipal().getBytes(StandardCharsets.UTF_8));
    }
}
