package com.hauth.cas.auth.ldap;

import com.hauth.cas.auth.config.AttributeNames;
import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

/**
 * @author hundanli
 */
public class PersonAttributesMapper implements AttributesMapper<LdapPerson> {
    @Override
    public LdapPerson mapFromAttributes(Attributes attributes) throws NamingException {
        LdapPerson ldapPerson = new LdapPerson();
        ldapPerson.setDistinguishedName((String) attributes.get(AttributeNames.DISTINGUISHED_NAME).get());
        ldapPerson.setSAMAccountName((String) attributes.get(AttributeNames.SAM_ACCOUNT_NAME).get());
        ldapPerson.setMail((String) attributes.get(AttributeNames.MAIL).get());
        ldapPerson.setMobile((String) attributes.get(AttributeNames.MOBILE).get());
        ldapPerson.setName((String) attributes.get(AttributeNames.NAME).get());
        ldapPerson.setEmployeeId((String) attributes.get(AttributeNames.EMPLOYEE_ID).get());
        return ldapPerson;
    }
}