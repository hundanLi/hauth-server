package com.hauth.auth.user;

import com.hauth.auth.dao.entity.AuthUser;
import lombok.Data;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/8 10:17
 */
@Data
public class UserAuthenticationResult {

    private String error;
    private AuthUser authUser;

    public UserAuthenticationResult(String error, AuthUser authUser) {
        this.error = error;
        this.authUser = authUser;
    }

    public UserAuthenticationResult() {
    }
}
