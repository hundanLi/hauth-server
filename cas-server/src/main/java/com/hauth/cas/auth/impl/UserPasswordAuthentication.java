package com.hauth.cas.auth.impl;

import com.hauth.cas.auth.Authentication;
import com.hauth.cas.auth.AuthenticationType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/19 23:18
 */
public class UserPasswordAuthentication implements Authentication {

    private final String username;
    private final String password;
    private boolean isAuthenticated = false;
    private String errorCode;
    private final Map<String, Object> attributes = new HashMap<>();


    public UserPasswordAuthentication(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public AuthenticationType getAuthenticationType() {
        return AuthenticationType.PASSWORD;
    }

    @Override
    public String getPrincipal() {
        return username;
    }

    @Override
    public String getCredential() {
        return password;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return new HashMap<>(attributes);
    }

    @Override
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes.putAll(attributes);
    }
}
