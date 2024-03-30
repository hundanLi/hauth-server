package com.hauth.cas.auth.ldap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.AuthenticatedLdapEntryContextMapper;
import org.springframework.ldap.core.LdapEntryIdentification;

import javax.naming.directory.DirContext;

/**
 * @author hundanli
 */
@Slf4j
public class AuthenticatedContextMapper implements AuthenticatedLdapEntryContextMapper<LdapPerson> {


    @Override
    public LdapPerson mapWithContext(DirContext ctx, LdapEntryIdentification ldapEntryIdentification) {
        if (ctx == null || ldapEntryIdentification == null) {
            return null;
        }
        LdapPerson ldapPerson = new LdapPerson();
        String absoluteName = ldapEntryIdentification.getAbsoluteName().toString();
        log.info("authenticated success, distinguishedName: {}", absoluteName);
        ldapPerson.setDistinguishedName(absoluteName);
        return ldapPerson;
    }
}
