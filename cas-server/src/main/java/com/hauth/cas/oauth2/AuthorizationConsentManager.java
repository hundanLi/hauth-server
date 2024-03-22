package com.hauth.cas.oauth2;

import com.hauth.cas.oauth2.dto.AuthorizeConsent;
import com.hauth.cas.oauth2.dto.AuthorizeRequest;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/20 10:30
 */
public interface AuthorizationConsentManager {

    String CONSENT_APPROVED = "allow";
    String CONSENT_REJECTED = "deny";


    /**
     * 检查是否已经授权同意
     * @param authorizeRequest 授权请求
     * @return 是否已同意
     */
    boolean hasConsent(AuthorizeRequest authorizeRequest);

    /**
     * 保存授权同意
     * @param authorizeRequest 授权请求
     */
    void saveConsent(AuthorizeRequest authorizeRequest);

    /**
     * 保存 code->consent对应关系
     * @param code authorization_code
     * @param consent authorize consent
     */
    void setConsent(String code, AuthorizeConsent consent);

    /**
     * 获取code->consent
     * @param code authorization_code
     * @return consent
     */
    AuthorizeConsent getConsent(String code);

    /**
     * 删除code->consent对应关系
     * @param code 授权码
     */
    void removeConsent(String code);


}
