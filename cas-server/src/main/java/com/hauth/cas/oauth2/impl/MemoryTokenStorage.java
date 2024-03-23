package com.hauth.cas.oauth2.impl;

import com.hauth.cas.oauth2.TokenManager;
import com.hauth.cas.oauth2.TokenStorage;
import com.hauth.cas.oauth2.dto.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/20 19:36
 */
@Slf4j
@Service
public class MemoryTokenStorage implements TokenStorage {

    private final Map<String, UserProfile> accessTokenUserMap = new ConcurrentHashMap<>();
    private final Map<String, UserProfile> refreshTokenUserMap = new ConcurrentHashMap<>();

    @Override
    public void saveAccessToken(String accessToken, UserProfile userProfile) {
        accessTokenUserMap.put(accessToken, userProfile);
    }

    @Override
    public void saveRefreshToken(String refreshToken, UserProfile userProfile) {
        refreshTokenUserMap.put(refreshToken, userProfile);
    }

    @Override
    public UserProfile getUserProfileByToken(String token) {
        if (token.startsWith(TokenManager.AT_PREFIX)) {
            return accessTokenUserMap.get(token);
        } else {
            return refreshTokenUserMap.get(token);
        }
    }
}
