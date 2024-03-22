package com.hauth.cas.oauth2;

import com.hauth.cas.oauth2.dto.AccessToken;
import com.hauth.cas.oauth2.dto.AccessTokenRequest;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/21 16:20
 */
public interface TokenRequestHandler {

    /**
     * 处理请求
     * @param accessTokenRequest token请求
     * @return token
     */
    AccessToken handleRequest(AccessTokenRequest accessTokenRequest);

    /**
     * 是否支持处理
     * @param grantType 授权类型，password,authorization_code,client_credentials
     * @return 是否支持
     */
    boolean supports(String grantType);

}
