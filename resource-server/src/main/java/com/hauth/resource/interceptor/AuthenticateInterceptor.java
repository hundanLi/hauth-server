package com.hauth.resource.interceptor;

import com.hauth.resource.config.OAuthProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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
    private static final String AUTHENTICATION = "AUTHENTICATION";
    private static final String STATE = "STATE";
    private static final String STATE_CALLBACK = "STATE_CALLBACK";

    private static final String AUTH_URL_TEMPLATE = "%s/oauth2.0/authorize?client_id=%s&redirect_uri=%s&response_type=code";

    @Autowired
    private OAuthProperties oAuthProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(true);

        if (session.getAttribute(oAuthProperties.getAuthFlag()) != null) {
            // 已登录
            return true;
        }
        String requestUri = request.getRequestURI();
        if (ROOT.equals(requestUri)) {
            if (Objects.equals(session.getAttribute(STATE), STATE_CALLBACK)) {
                // oauth callback
                session.removeAttribute(STATE);
                return true;
            }
        }
        log.warn("unauthorized request: {}", requestUri);
        session.setAttribute(STATE, STATE_CALLBACK);
        String authUrl = constructAuthUrl();
        response.sendRedirect(authUrl);
        return false;
    }


    private String constructAuthUrl() {
        return String.format(AUTH_URL_TEMPLATE, oAuthProperties.getServer(), oAuthProperties.getClientId(), oAuthProperties.getRedirectUri());
    }
}
