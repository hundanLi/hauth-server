package com.hauth.cas.auth.impl;

import com.hauth.cas.auth.Authentication;
import com.hauth.cas.auth.AuthenticationProvider;
import com.hauth.cas.auth.AuthenticationType;
import com.hauth.cas.auth.config.AttributeNames;
import com.hauth.cas.auth.config.MemoryAuthProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/18 19:14
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "auth.inmemory.enabled", havingValue = "true")
public class MemoryAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private MemoryAuthProperties memoryAuthProperties;

    @Override
    public Authentication authenticate(Authentication authentication) {
        log.info("try to authenticate user: {}", authentication.getPrincipal());
        if (Objects.equals(authentication.getPrincipal(), memoryAuthProperties.getUsername())
                && Objects.equals(authentication.getCredential(), memoryAuthProperties.getPassword())) {
            authentication.setAuthenticated(true);
            retrieveAttributes(authentication);
            return authentication;
        } else {
            authentication.setAuthenticated(false);
            authentication.setErrorCode("INVALID_CREDENTIAL");
        }
        return authentication;
    }

    @Override
    public boolean supports(AuthenticationType authenticationType) {
        return AuthenticationType.PASSWORD.equals(authenticationType);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private void retrieveAttributes(Authentication authentication) {
        Map<String, Object> attributes = new HashMap<>(8);
        attributes.put(AttributeNames.MAIL, memoryAuthProperties.getMail());
        attributes.put(AttributeNames.MOBILE, memoryAuthProperties.getMobile());
        attributes.put(AttributeNames.NAME, memoryAuthProperties.getName());
        attributes.put(AttributeNames.EMPLOYEE_ID, memoryAuthProperties.getEmployeeID());
        attributes.put(AttributeNames.SAM_ACCOUNT_NAME, memoryAuthProperties.getSAMAccountName());
        attributes.put(AttributeNames.DISTINGUISHED_NAME, memoryAuthProperties.getDistinguishedName());
        authentication.setAttributes(attributes);

    }
}
