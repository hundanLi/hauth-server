package com.hauth.auth.user;


/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/8 10:13
 */
public interface UserAuthenticateService {


    /**
     * 用户认证
     *
     * @param userAuthentication 认证信息
     * @return 认证结果
     */
    UserAuthenticationResult authenticate(UserAuthentication userAuthentication);


    /**
     * 是否支持
     *
     * @param authType 认证类型
     * @return 是否支持
     */
    default boolean supports(String authType) {
        return true;
    }
}
