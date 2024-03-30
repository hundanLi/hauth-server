package com.hauth.cas.auth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.ldap.LdapProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/27 13:30
 */
@Configuration
public class AuthenticationConfig {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    @Bean
    @ConditionalOnMissingBean
    public LdapContextSource ldapContextSource(LdapProperties properties, Environment environment) {
        LdapContextSource source = new LdapContextSource();
        PropertyMapper propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        propertyMapper.from(properties.getUsername()).to(source::setUserDn);
        propertyMapper.from(properties.getPassword()).to(source::setPassword);
        propertyMapper.from(properties.getAnonymousReadOnly()).to(source::setAnonymousReadOnly);
        propertyMapper.from(properties.getBase()).to(source::setBase);
        propertyMapper.from(properties.determineUrls(environment)).to(source::setUrls);
        propertyMapper.from(properties.getBaseEnvironment()).to(
                (baseEnvironment) -> source.setBaseEnvironmentProperties(Collections.unmodifiableMap(baseEnvironment)));
        source.setReferral("follow");
        return source;
    }

    @Bean
    @ConditionalOnMissingBean(LdapOperations.class)
    public LdapTemplate ldapTemplate(ContextSource contextSource) {
        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
        ldapTemplate.setIgnorePartialResultException(true);
        return ldapTemplate;
    }

}
