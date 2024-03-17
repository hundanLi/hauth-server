package com.hauth.auth.user;

import lombok.Data;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/8 10:16
 */
@Data
public class UserAuthentication {

    private String username;
    private String password;
    private String authType;

}
