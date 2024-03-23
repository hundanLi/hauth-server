package com.hauth.cas.oauth2.util;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/22 18:57
 */
@Slf4j
public class HashUtil {

    private HashUtil() {

    }

    public String sha256(String data) {
        try {
            // 创建MessageDigest实例，指定为SHA-256算法
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // 执行哈希计算，返回结果为字节数据
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException exception) {
            log.error("compute hs256 hash error: {}", exception.getMessage());
        }
        return data;
    }
}
