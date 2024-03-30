package com.hauth.cas.auth.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hauth.cas.auth.Authentication;
import com.hauth.cas.auth.AuthenticationProvider;
import com.hauth.cas.auth.AuthenticationType;
import com.hauth.cas.auth.config.AttributeNames;
import com.hauth.cas.constant.ErrorCodeConstant;
import com.hauth.cas.dao.entity.CasAccount;
import com.hauth.cas.dao.service.ICasAccountService;
import com.hauth.cas.error.InvalidCredentialException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/27 13:27
 */
@Slf4j
@Service
public class MysqlAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private ICasAccountService casAccountService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public Authentication authenticate(Authentication authentication) {
        String accountName = authentication.getPrincipal();
        String password = authentication.getCredential();
        LambdaQueryWrapper<CasAccount> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(CasAccount::getAccountName, accountName);
        queryWrapper.last("limit 1");
        CasAccount casAccount = casAccountService.getOne(queryWrapper);
        if (casAccount == null) {
            authentication.setAuthenticated(false);
            return authentication;
        }
        if (passwordEncoder.matches(password, casAccount.getPassword())) {
            authentication.setAuthenticated(true);
            authentication.setAttributes(collectAttributes(casAccount));
        } else {
            authentication.setAuthenticated(false);
            authentication.setErrorCode(ErrorCodeConstant.INVALID_CREDENTIAL);
        }
        return authentication;
    }

    @Override
    public boolean supports(AuthenticationType authenticationType) {
        return AuthenticationType.PASSWORD.equals(authenticationType);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private Map<String, Object> collectAttributes(CasAccount casAccount) {
        Map<String, Object> attributes = new HashMap<>(8);
        attributes.put(AttributeNames.SAM_ACCOUNT_NAME, casAccount.getAccountName());
        attributes.put(AttributeNames.NAME, casAccount.getDisplayName());
        attributes.put(AttributeNames.EMPLOYEE_ID, casAccount.getAccountId());
        attributes.put(AttributeNames.MAIL, casAccount.getMail());
        attributes.put(AttributeNames.MOBILE, casAccount.getMobile());
        return attributes;
    }
}
