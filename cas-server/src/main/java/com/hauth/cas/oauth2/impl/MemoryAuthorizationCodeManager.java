package com.hauth.cas.oauth2.impl;

import com.hauth.cas.oauth2.AuthorizationCodeManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/19 0:25
 */
@Slf4j
@Service
public class MemoryAuthorizationCodeManager implements AuthorizationCodeManager {

    private final ThreadLocal<SecureRandom> secureRandom = ThreadLocal.withInitial(SecureRandom::new);

    private final Map<String, String> codeMap = new ConcurrentHashMap<>();

    @Override
    public String generateCode(String clientId) {
        byte[] bytes = new byte[32];
        secureRandom.get().nextBytes(bytes);
        String code = Base64.getUrlEncoder().encodeToString(bytes);
        codeMap.put(clientId, code);
        return code;
    }

    @Override
    public boolean checkCode(String clientId, String code) {
        return Objects.equals(codeMap.get(clientId), code);
    }


    @Override
    public void setCodeChallenge(String clientId, String codeChallenge, String codeChallengeMethod) {
        // TODO
    }

    @Override
    public boolean checkCodeVerifier(String clientId, String codeVerifier) {
        // TODO
        return true;
    }

}
