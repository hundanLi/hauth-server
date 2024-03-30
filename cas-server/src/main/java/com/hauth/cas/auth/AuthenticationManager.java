package com.hauth.cas.auth;

import com.hauth.cas.constant.AuthenticateConstant;
import com.hauth.cas.auth.ticket.TicketStore;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/19 23:11
 */
@Slf4j
@Service
public class AuthenticationManager {

    @Autowired
    private TicketStore ticketStore;

    @Autowired
    private List<AuthenticationProvider> authenticationProviders;


    public boolean hasLogin(HttpServletRequest request) {
        if (request.getSession(false) == null) {
            return false;
        }
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (Objects.equals(cookie.getName(), AuthenticateConstant.COOKIE_TGC)) {
                String ticketGrantTicket = ticketStore.getTicketGrantTicket(request.getSession(false).getId());
                if (ticketGrantTicket != null && Objects.equals(cookie.getValue(), ticketGrantTicket)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getTicketGrantCookie(HttpServletRequest request) {
        if (request.getSession(false) == null) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (Objects.equals(cookie.getName(), AuthenticateConstant.COOKIE_TGC)) {
                String ticketGrantTicket = ticketStore.getTicketGrantTicket(request.getSession(false).getId());
                if (ticketGrantTicket != null && Objects.equals(cookie.getValue(), ticketGrantTicket)) {
                    return ticketGrantTicket;
                }
            }
        }
        return null;
    }

    public Authentication getUserAuthentication(HttpServletRequest request) {
        return (Authentication) request.getSession(false).getAttribute(AuthenticateConstant.PRINCIPAL);
    }

    public Authentication authenticate(Authentication authentication) {
        for (AuthenticationProvider provider : authenticationProviders) {
            if (provider.supports(authentication.getAuthenticationType())) {
                long currentTime = System.currentTimeMillis();
                provider.authenticate(authentication);
                if (authentication.isAuthenticated()) {
                    log.info("authenticate success with handler {}, cost time: {}ms", provider.getClass().getSimpleName(), (System.currentTimeMillis() - currentTime));
                    return authentication;
                }
            }
        }
        authentication.setAuthenticated(false);
        return authentication;
    }
}
