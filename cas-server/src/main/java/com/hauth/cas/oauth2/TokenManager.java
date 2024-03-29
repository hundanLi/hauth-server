package com.hauth.cas.oauth2;

import com.hauth.cas.oauth2.dto.UserProfile;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/19 20:11
 */
public interface TokenManager {

    String AT_PREFIX = "AT-";
    String RT_PREFIX = "RT-";
    String BEARER = "Bearer";

    /**
     * 生成accessToken
     * @param userProfile 用户信息
     * @return accessToken
     */
    String generateAccessToken(UserProfile userProfile);


    /**
     * 生成refreshToken
     * @param userProfile 用户信息
     * @return refreshToken
     */
    String generateRefreshToken(UserProfile userProfile);

    /**
     * 校验accessToken
     * @param accessToken token
     * @return 用户信息
     */
    UserProfile validateAccessToken(String accessToken);

    /**
     * 校验refreshToken
     * @param refreshToken token
     * @return 用户信息
     */
    UserProfile validateRefreshToken(String refreshToken);
}
