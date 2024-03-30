package com.hauth.cas.auth.ldap;

import lombok.Data;
import org.springframework.ldap.odm.annotations.Entry;

/**
 * @author hundanli
 */
@Data
@Entry(objectClasses = "user")
public class LdapPerson {
    private String sAMAccountName;
    private String mail;
    private String mobile;
    private String name;
    private String employeeId;
    private String distinguishedName;
}
