package com.hauth.cas.oauth2;


import com.hauth.cas.oauth2.dto.UserProfile;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/20 19:26
 */
public interface TokenStorage {

    /**
     * 保存accessToken
     *
     * @param accessToken token
     * @param userProfile 用户信息
     */
    void saveAccessToken(String accessToken, UserProfile userProfile);

    /**
     * 移除accessToken
     *
     * @param accessToken token
     */
    void removeAccessToken(String accessToken);

    /**
     * 保存refreshToken
     *
     * @param refreshToken refreshToken
     * @param userProfile  用户信息
     */
    void saveRefreshToken(String refreshToken, UserProfile userProfile);


    /**
     * 移除refreshToken
     *
     * @param refreshToken token
     */
    void removeRefreshToken(String refreshToken);

    /**
     * 获取用户
     *
     * @param accessToken token
     * @return 用户信息
     */
    UserProfile getUserProfileByToken(String accessToken);

}
