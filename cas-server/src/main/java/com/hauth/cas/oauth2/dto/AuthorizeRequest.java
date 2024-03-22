package com.hauth.cas.oauth2.dto;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/18 19:39
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizeRequest {

    private String responseType;
    private String clientId;
    private String redirectUri;
    private String codeChallenge;
    private String codeChallengeMethod;
    private String state;

    private String clientName;
    private String user;
    private Map<String, Object> userAttributes;
    private String consent;
    private String scope;
    private HttpServletRequest request;
    private HttpServletResponse response;

}
