package com.hauth.resource.interceptor;

import com.hauth.resource.config.OAuthProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/21 23:31
 */
@Slf4j
@Service
public class AuthenticateInterceptor implements HandlerInterceptor {

    private static final String ROOT = "/";
    private static final String SESSION_STATE = "SESSION_STATE";
    private static final String STATE_CALLBACK = "STATE_CALLBACK";

    private static final String AUTH_URL_TEMPLATE = "%s/oauth2.0/authorize?client_id=%s&redirect_uri=%s&response_type=code&code_challenge=%s&code_challenge_method=S256&state=%s";

    private final ThreadLocal<SecureRandom> secureRandom = ThreadLocal.withInitial(SecureRandom::new);


    @Autowired
    private OAuthProperties oAuthProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(true);

        if (session.getAttribute(oAuthProperties.getAuthName()) != null) {
            // 已登录
            log.info("authorized request: {}", request.getRequestURI());
            return true;
        }
        String requestUri = request.getRequestURI();
        if (ROOT.equals(requestUri)) {
            if (Objects.equals(session.getAttribute(SESSION_STATE), STATE_CALLBACK)) {
                // oauth callback
                log.info("oauth callback, remove {} attribute.", SESSION_STATE);
                session.removeAttribute(SESSION_STATE);
                return true;
            }
        }
        session.setAttribute(SESSION_STATE, STATE_CALLBACK);
        String authUrl = constructAuthUrl(request);
        log.warn("unauthorized request: {}, redirect to login: {}", requestUri, authUrl);
        response.sendRedirect(authUrl);
        return false;
    }


    private String constructAuthUrl(HttpServletRequest request) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[16];
        secureRandom.get().nextBytes(bytes);
        String state = Base64.getUrlEncoder().encodeToString(bytes);
        request.getSession(false).setAttribute(oAuthProperties.getStateName(), state);
//        bytes = new byte[16];
        secureRandom.get().nextBytes(bytes);
        String codeVerifier = Base64.getUrlEncoder().encodeToString(bytes);
        // 创建MessageDigest实例，指定为SHA-256算法
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        // 执行哈希计算，返回结果为字节数据
        byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
        String codeChallenge = Base64.getUrlEncoder().encodeToString(hash);
        request.getSession().setAttribute(oAuthProperties.getCodeVerifierName(), codeVerifier);
        return String.format(AUTH_URL_TEMPLATE, oAuthProperties.getServer(), oAuthProperties.getClientId(), oAuthProperties.getRedirectUri(), codeChallenge, state);
    }
}
