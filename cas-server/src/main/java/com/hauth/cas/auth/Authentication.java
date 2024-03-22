package com.hauth.cas.auth;

import java.util.Map;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/18 14:52
 */
public interface Authentication {

    /**
     * 认证类型，如密码、手机号验证码、邮箱验证码...
     *
     * @return 认证类型
     */
    AuthenticationType getAuthenticationType();

    /**
     * 用户标识，如用户名、手机号、邮箱...
     *
     * @return 用户标识
     */
    String getPrincipal();

    /**
     * 用户凭证，如用户密码、验证码...
     *
     * @return 用户凭证
     */
    String getCredential();

    /**
     * 是否认证成功
     *
     * @return 认证结果
     */
    boolean isAuthenticated();

    /**
     * 设置认证结果
     *
     * @param isAuthenticated 认证结果
     */
    void setAuthenticated(boolean isAuthenticated);


    /**
     * 获取错误码
     *
     * @return 错误码
     */
    String getErrorCode();

    /**
     * 设置错误码
     *
     * @param errorCode 错误码
     */
    void setErrorCode(String errorCode);

    /**
     * 获取用户属性
     *
     * @return 用户属性
     */
    Map<String, Object> getAttributes();

    /**
     * 设置属性
     * @param attributes 属性
     */
    void setAttributes(Map<String, Object> attributes);
}
