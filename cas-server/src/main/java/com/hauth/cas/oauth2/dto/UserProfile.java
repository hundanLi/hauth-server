package com.hauth.cas.oauth2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/20 15:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    /**
     * 用户标识，如用户名、手机号、邮箱...
     */
    private String principal;
    private String id;
    private String clientId;
    /**
     * 获取用户属性
     */
    private Map<String, Object> attributes;
    /**
     * OAuth2.0 Client服务的redirect_uri
     */
    private String service;
}
