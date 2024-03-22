package com.hauth.cas.oauth2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/20 13:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizeConsent {

    private String clientId;
    private String user;
    private String scope;
    private Map<String, Object> attributes;

}
