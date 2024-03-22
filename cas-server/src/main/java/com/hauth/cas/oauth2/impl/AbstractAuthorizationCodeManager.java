package com.hauth.cas.oauth2.impl;

import com.hauth.cas.oauth2.AuthorizationCodeManager;
import com.hauth.cas.oauth2.config.OAuth20Constant;
import com.hauth.cas.oauth2.dto.CodeChallenge;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/22 13:47
 */
@Slf4j
public abstract class AbstractAuthorizationCodeManager implements AuthorizationCodeManager {

    private final ThreadLocal<SecureRandom> secureRandom = ThreadLocal.withInitial(SecureRandom::new);

    private final HashedWheelTimer wheelTimer = new HashedWheelTimer();

    private final Map<String, Timeout> codeTimeoutMap = new ConcurrentHashMap<>();

    @Override
    public String generateCode(String clientId) {
        byte[] bytes = new byte[32];
        secureRandom.get().nextBytes(bytes);
        String code = Base64.getUrlEncoder().encodeToString(bytes);
        storeCodeInternal(clientId, code);
        setTimeout(code);
        return code;
    }

    @Override
    public void removeCode(String code) {
        removeCodeInternal(code);
        cancelTimer(code);
    }

    @Override
    public void setCodeChallenge(String code, String codeChallenge, String codeChallengeMethod) {
        CodeChallenge challenge = new CodeChallenge().setCodeChallenge(codeChallenge)
                .setCodeChallengeMethod(codeChallengeMethod);
        storeCodeChallengeInternal(code, challenge);

    }

    @Override
    public boolean checkCodeVerifier(String code, String codeVerifier) {
        CodeChallenge challenge = getCodeChallengeInternal(code);
        if (challenge == null) {
            return true;
        }
        String codeChallenge = challenge.getCodeChallenge();
        String method = challenge.getCodeChallengeMethod();
        if (method == null) {
            method = OAuth20Constant.PKCE_METHOD_PLAIN;
        }
        boolean result = false;
        switch (method) {
            case OAuth20Constant.PKCE_METHOD_S256:
                try {
                    // 创建MessageDigest实例，指定为SHA-256算法
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    // 执行哈希计算，返回结果为字节数据
                    byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
                    String serverChallenge = Base64.getUrlEncoder().encodeToString(hash);
                    log.info("check code_verifier using S256, serverChallenge:{}, clientChallenge:{}",
                            serverChallenge, codeChallenge);
                    result = Objects.equals(serverChallenge, codeChallenge);
                } catch (NoSuchAlgorithmException exception) {
                    log.error("calculate SHA256 error: {}", exception.getMessage());
                }
                break;
            case OAuth20Constant.PKCE_METHOD_PLAIN:
            default:
                result = Objects.equals(codeVerifier, codeChallenge);

        }
        return result;
    }

    protected void cancelTimer(String code) {
        Timeout timeout = codeTimeoutMap.get(code);
        if (timeout != null) {
            timeout.cancel();
        }
    }

    private void setTimeout(String code) {
        Timeout timeout = wheelTimer.newTimeout(new CodeTimerTask(this, code), 60, TimeUnit.SECONDS);
        codeTimeoutMap.put(code, timeout);
    }

    private static class CodeTimerTask implements TimerTask {

        private final AuthorizationCodeManager codeManager;
        private final String code;

        public CodeTimerTask(AuthorizationCodeManager codeManager, String code) {
            this.codeManager = codeManager;
            this.code = code;
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            if (!timeout.isCancelled()) {
                log.info("authorization_code expired, code:{}", code);
                codeManager.removeCode(code);
            }
        }
    }

    /**
     * 存储code-clientId映射关系
     * @param clientId clientId
     * @param code 授权码
     */
    protected abstract void storeCodeInternal(String clientId, String code);

    /**
     * 删除code
     * @param code 授权码
     */
    protected abstract void removeCodeInternal(String code);


    /**
     * 存储PKCE
     * @param code 授权码
     * @param codeChallenge PKCE
     */
    protected abstract void storeCodeChallengeInternal(String code, CodeChallenge codeChallenge);

    /**
     * 获取PKCE
     * @param code 授权码
     * @return PKCE
     */
    protected abstract CodeChallenge getCodeChallengeInternal(String code);
}
