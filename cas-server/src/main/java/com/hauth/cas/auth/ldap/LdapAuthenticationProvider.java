package com.hauth.cas.auth.ldap;

import com.hauth.cas.auth.Authentication;
import com.hauth.cas.auth.AuthenticationProvider;
import com.hauth.cas.auth.AuthenticationType;
import com.hauth.cas.auth.config.AttributeNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/27 14:52
 */
@Slf4j
@Service
public class LdapAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private LdapTemplate ldapTemplate;

    private final String[] attributesToReturn = {
            "sAMAccountName",
            "mail",
            "mobile",
            "name",
            "employeeId",
            "distinguishedName"
    };
    private static final String OBJECT_CLASS_KEY = "objectClass";
    private static final String SAM_ACCOUNT_NAME_KEY = "sAMAccountName";


    @Override
    public Authentication authenticate(Authentication authentication) {
        String accountName = authentication.getPrincipal();
        String password = authentication.getCredential();
        LdapPerson ldapPerson = authenticateUser(accountName, password);
        if (ldapPerson != null && ldapPerson.getDistinguishedName() != null) {
            ldapPerson = searchPerson(accountName);
            authentication.setAuthenticated(true);
            authentication.setAttributes(collectAttributes(ldapPerson));
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


    private LdapPerson searchPerson(String accountName) {
        return ldapTemplate.searchForObject(
                query().attributes(attributesToReturn)
                        .where(SAM_ACCOUNT_NAME_KEY).is(accountName),
                new PersonContextMapper());
    }


    private LdapPerson authenticateUser(String accountName, String password) {
        ContainerCriteria criteria = query()
                .where(SAM_ACCOUNT_NAME_KEY).is(accountName);
        return ldapTemplate.authenticate(criteria, password, new AuthenticatedContextMapper());
    }

    private Map<String, Object> collectAttributes(LdapPerson ldapPerson) {
        Map<String, Object> attributes = new HashMap<>(8);
        attributes.put(AttributeNames.SAM_ACCOUNT_NAME, ldapPerson.getSAMAccountName());
        attributes.put(AttributeNames.NAME, ldapPerson.getName());
        attributes.put(AttributeNames.EMPLOYEE_ID, ldapPerson.getEmployeeId());
        attributes.put(AttributeNames.MAIL, ldapPerson.getMail());
        attributes.put(AttributeNames.MOBILE, ldapPerson.getMobile());
        return attributes;
    }

}
