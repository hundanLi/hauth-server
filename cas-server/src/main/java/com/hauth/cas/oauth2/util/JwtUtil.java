package com.hauth.cas.oauth2.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * @author hundanli
 */
@Slf4j
public class JwtUtil {

    private JwtUtil() {

    }

    public static String generateToken(String secret, Map<String, Object> claims, long expirationMs) {
        Key key = new SecretKeySpec(Base64.getUrlDecoder().decode(secret), SignatureAlgorithm.HS256.getJcaName());
        return Jwts.builder().setSubject("hundanli").addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, key).compact();
    }

    public static Claims validateToken(String secret, String jws) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(Base64.getUrlDecoder().decode(secret))
                    .build()
                    .parseClaimsJws(jws);
            return claimsJws.getBody();
        } catch (Exception exception) {
            log.error("parse jwt token error: {}", exception.getMessage());
            throw new IllegalArgumentException("Invalid token: " + jws);
        }
    }

    public static String generateSecret() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().encodeToString(bytes);
    }

}