package com.hauth.cas.oauth2.impl;

import com.hauth.cas.oauth2.TokenManager;
import com.hauth.cas.oauth2.config.OAuth20Properties;
import com.hauth.cas.oauth2.dto.UserProfile;
import com.hauth.cas.oauth2.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/20 15:10
 */
@Slf4j
@Service
public class SimpleTokenManager implements TokenManager {

    @Autowired
    private OAuth20Properties oAuth20Properties;

    @Override
    public String generateAccessToken(UserProfile userProfile) {
        String principal = userProfile.getPrincipal();
        Map<String, Object> claims = new HashMap<>(4);
        claims.put("username", principal);
        claims.put("type", "access_token");
        claims.put("clientId", userProfile.getClientId());
        String token = JwtUtil.generateToken(oAuth20Properties.getTokenSecret(), claims, oAuth20Properties.getTokenTimeout().toMillis());
        return TokenManager.AT_PREFIX + token;
    }

    @Override
    public String generateRefreshToken(UserProfile userProfile) {
        String principal = userProfile.getPrincipal();
        Map<String, Object> claims = new HashMap<>(4);
        claims.put("username", principal);
        claims.put("type", "refresh_token");
        claims.put("clientId", userProfile.getClientId());
        String token = JwtUtil.generateToken(oAuth20Properties.getTokenSecret(), claims, oAuth20Properties.getTokenTimeout().toMillis());
        return TokenManager.RT_PREFIX + token;
    }

    @Override
    public UserProfile validateAccessToken(String accessToken) {
        if (accessToken == null || !accessToken.startsWith(TokenManager.AT_PREFIX)) {
            return null;
        }
        Claims claims = JwtUtil.validateToken(oAuth20Properties.getTokenSecret(), accessToken.substring(TokenManager.AT_PREFIX.length()));
        return getUserProfile(claims);
    }

    @Override
    public UserProfile validateRefreshToken(String refreshToken) {
        if (refreshToken == null || !refreshToken.startsWith(TokenManager.RT_PREFIX)) {
            return null;
        }
        Claims claims = JwtUtil.validateToken(oAuth20Properties.getTokenSecret(), refreshToken.substring(TokenManager.RT_PREFIX.length()));
        return getUserProfile(claims);
    }

    private UserProfile getUserProfile(Claims claims) {
        if (claims == null) {
            return null;
        } else {
            String username = claims.get("username", String.class);
            String clientId = claims.get("clientId", String.class);
            return UserProfile.builder()
                    .clientId(clientId)
                    .id(username)
                    .principal(username).build();
        }
    }
}
