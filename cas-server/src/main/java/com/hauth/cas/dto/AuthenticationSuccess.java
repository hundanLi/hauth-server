package com.hauth.cas.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author hundanli
 */
@Data
public class AuthenticationSuccess {
    String user;
    Map<String, Object> attributes;
}