package com.hauth.cas.error;

import lombok.Getter;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/27 13:41
 */
@Getter
public class InvalidCredentialException extends RuntimeException {

    private final String user;

    public InvalidCredentialException(String message, String user) {
        super(message);
        this.user = user;
    }

}
