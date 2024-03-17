package com.hauth.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/8 22:30
 */
@Configuration
@ConfigurationProperties(prefix = "auth")
public class AuthConfigProperties {

    private boolean quickstart;
    private boolean jdbc;
    private boolean password;

    public boolean isQuickstart() {
        return quickstart;
    }

    public void setQuickstart(boolean quickstart) {
        this.quickstart = quickstart;
    }

    public boolean isJdbc() {
        return jdbc;
    }

    public void setJdbc(boolean jdbc) {
        this.jdbc = jdbc;
    }

    public boolean isPassword() {
        return password;
    }

    public void setPassword(boolean password) {
        this.password = password;
    }
}
