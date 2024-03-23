package com.hauth.cas.oauth2.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    void generateSecret() {
        String secret = JwtUtil.generateSecret();
        System.out.println(secret);
    }
}