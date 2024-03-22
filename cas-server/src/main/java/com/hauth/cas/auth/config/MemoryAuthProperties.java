package com.hauth.cas.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/18 19:16
 */
@Data
@Component
@ConfigurationProperties(prefix = "auth.inmemory")
public class MemoryAuthProperties {

    private String username;
    private String password;
    private String mail;
    private String mobile;
    private String employeeID;
    private String name;
    private String sAMAccountName;
    private String distinguishedName;
    private boolean enabled;

}
