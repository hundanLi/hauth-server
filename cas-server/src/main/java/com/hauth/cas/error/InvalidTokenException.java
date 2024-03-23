package com.hauth.cas.error;

import lombok.Getter;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/23 15:12
 */
@Getter
public class InvalidTokenException extends RuntimeException {
    private final String token;

    public InvalidTokenException(String token) {
        super("Invalid token: " + token);
        this.token = token;
    }

}
