package com.hauth.cas.auth.ldap;

import com.hauth.cas.auth.config.AttributeNames;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;

/**
 * @author hundanli
 */
public class PersonContextMapper extends AbstractContextMapper<LdapPerson> {

    @Override
    protected LdapPerson doMapFromContext(DirContextOperations contextAdapter) {
        LdapPerson ldapPerson = new LdapPerson();
        ldapPerson.setSAMAccountName(contextAdapter.getStringAttribute(AttributeNames.SAM_ACCOUNT_NAME));
        ldapPerson.setMail(contextAdapter.getStringAttribute(AttributeNames.MAIL));
        ldapPerson.setMobile(contextAdapter.getStringAttribute(AttributeNames.MOBILE));
        ldapPerson.setName(contextAdapter.getStringAttribute(AttributeNames.NAME));
        ldapPerson.setEmployeeId(contextAdapter.getStringAttribute(AttributeNames.EMPLOYEE_ID));
        ldapPerson.setDistinguishedName(contextAdapter.getStringAttribute(AttributeNames.DISTINGUISHED_NAME));
        return ldapPerson;
    }

}