package com.hauth.cas.client;

import lombok.Data;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/18 19:52
 */
@Data
public class AuthClient {

    private String clientName;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String type;
    private String scope;
    private Boolean enabled;
    private Boolean skipConsent;

}
