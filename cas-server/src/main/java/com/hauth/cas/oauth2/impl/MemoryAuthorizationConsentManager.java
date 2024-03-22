package com.hauth.cas.oauth2.impl;

import com.hauth.cas.oauth2.AuthorizationConsentManager;
import com.hauth.cas.oauth2.dto.AuthorizeConsent;
import com.hauth.cas.oauth2.dto.AuthorizeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/20 10:44
 */
@Slf4j
@Service
public class MemoryAuthorizationConsentManager implements AuthorizationConsentManager {


    private final Map<String, Map<String, AuthorizeConsent>> userConsentsMap = new ConcurrentHashMap<>();

    private final Map<String, AuthorizeConsent> codeConsentMap = new ConcurrentHashMap<>();

    @Override
    public boolean hasConsent(AuthorizeRequest authorizeRequest) {
        Map<String, AuthorizeConsent> consentMap = userConsentsMap.get(authorizeRequest.getUser());
        if (consentMap == null) {
            return false;
        }
        AuthorizeConsent consent = consentMap.get(authorizeRequest.getClientId());
        if (consent == null) {
            return false;
        }
        // TODO 检查scope
        return true;

    }

    @Override
    public void saveConsent(AuthorizeRequest authorizeRequest) {
        Map<String, AuthorizeConsent> consentMap = userConsentsMap.compute(authorizeRequest.getUser(),
                (key, val) -> val != null ? val : new ConcurrentHashMap<>(8));
        AuthorizeConsent consent = AuthorizeConsent.builder()
                .user(authorizeRequest.getUser())
                .clientId(authorizeRequest.getClientId())
                .scope(authorizeRequest.getScope())
                .build();
        consentMap.put(authorizeRequest.getClientId(), consent);

    }

    @Override
    public void setConsent(String code, AuthorizeConsent consent) {
        codeConsentMap.put(code, consent);
    }

    @Override
    public AuthorizeConsent getConsent(String code) {
        return codeConsentMap.get(code);
    }

    @Override
    public void removeConsent(String code) {
        codeConsentMap.remove(code);
    }
}
