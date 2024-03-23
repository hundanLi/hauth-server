package com.hauth.cas.oauth2.config;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/19 0:01
 */
public interface OAuth20GrantType {

    String RESPONSE_TYPE_CODE = "code";
    String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    String GRANT_TYPE_RESOURCE_OWNER = "password";
    String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

}
