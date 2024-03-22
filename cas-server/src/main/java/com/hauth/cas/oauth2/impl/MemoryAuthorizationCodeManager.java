package com.hauth.cas.oauth2.impl;

import com.hauth.cas.oauth2.dto.CodeChallenge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
public class MemoryAuthorizationCodeManager extends AbstractAuthorizationCodeManager {


    private final Map<String, String> codeMap = new ConcurrentHashMap<>();

    private final Map<String, CodeChallenge> codeChallengeMap = new ConcurrentHashMap<>();

    @Override
    protected void storeCodeInternal(String clientId, String code) {
        codeMap.put(code, clientId);
    }

    @Override
    public boolean checkCode(String clientId, String code) {
        return Objects.equals(codeMap.get(code), clientId);
    }


    @Override
    public void storeCodeChallengeInternal(String code, CodeChallenge codeChallenge) {
        // 存储code_challenge
        codeChallengeMap.put(code, codeChallenge);
    }

    @Override
    protected CodeChallenge getCodeChallengeInternal(String code) {
        return codeChallengeMap.get(code);
    }

    @Override
    public void removeCodeInternal(String code) {
        codeMap.remove(code);
        codeChallengeMap.remove(code);
    }
}
