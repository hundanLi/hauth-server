package com.hauth.cas.dto;

import lombok.Data;

/**
 * @author hundanli
 */
@Data
public class AuthenticationFailure {
    String code;
    String description;
}