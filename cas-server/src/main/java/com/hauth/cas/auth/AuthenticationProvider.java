package com.hauth.cas.auth;

import org.springframework.core.Ordered;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/18 14:52
 */
public interface AuthenticationProvider extends Ordered {


    /**
     * 用户认证
     *
     * @param authentication 用户凭证
     * @return 认证结果
     */
    Authentication authenticate(Authentication authentication);


    /**
     * 是否支持认证
     *
     * @param authenticationType 认证类型
     * @return 是否支持
     */
    boolean supports(AuthenticationType authenticationType);

}
