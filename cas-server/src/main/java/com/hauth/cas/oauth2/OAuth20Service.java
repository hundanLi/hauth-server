package com.hauth.cas.oauth2;

import com.hauth.cas.client.AuthClient;
import com.hauth.cas.client.AuthClientStore;
import com.hauth.cas.oauth2.config.OAuth20Constant;
import com.hauth.cas.oauth2.config.OAuth20GrantType;
import com.hauth.cas.oauth2.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/18 23:46
 */
@Slf4j
@Service
public class OAuth20Service {

    @Autowired
    private AuthClientStore authClientStore;

    @Autowired
    private AuthorizationCodeManager authorizationCodeManager;

    @Autowired
    private AuthorizationConsentManager authorizationConsentManager;

    @Autowired
    private TokenStorage tokenStorage;

    @Autowired
    private List<TokenRequestHandler> tokenRequestHandlers;

    private final Map<String, AuthorizeRequest> authorizeRequestMap = new ConcurrentHashMap<>();

    public void checkAuthorizeRequest(AuthorizeRequest authorizeRequest) {
        checkAuthorizeRequestParam(authorizeRequest);
        AuthClient authClient = authClientStore.findByClientId(authorizeRequest.getClientId());
        authorizeRequest.setClientName(authClient.getClientName());
        String sessionId = authorizeRequest.getRequest().getSession(false).getId();
        authorizeRequestMap.put(sessionId, authorizeRequest);
    }

    public AuthorizeRequest getAuthorizationRequest(HttpServletRequest request) {
        String sessionId = request.getSession(false).getId();
        return authorizeRequestMap.get(sessionId);
    }

    public boolean hasConsent(AuthorizeRequest authorizeRequest) {
        checkAuthorizeRequestParam(authorizeRequest);
        AuthClient authClient = authClientStore.findByClientId(authorizeRequest.getClientId());
        if (authClient.getSkipConsent()) {
            log.info("auth client has been set to skip consent, client_id:{}", authorizeRequest.getClientId());
            return true;
        }
        return authorizationConsentManager.hasConsent(authorizeRequest);
    }

    public String authorizationConsent(AuthorizeRequest authorizeRequest) {
        checkAuthorizeRequestParam(authorizeRequest);
        if (AuthorizationConsentManager.CONSENT_APPROVED.equals(authorizeRequest.getConsent())) {
            log.info("use approved authorization request for client_id:{}", authorizeRequest.getClientId());
            // 持久化保存授权同意信息
            authorizationConsentManager.saveConsent(authorizeRequest);
            String code = authorizationCodeManager.generateCode(authorizeRequest.getClientId());
            // state 和 code_challenge
            if (authorizeRequest.getCodeChallenge() != null) {
                authorizationCodeManager.setCodeChallenge(authorizeRequest.getClientId(), authorizeRequest.getCodeChallenge(), authorizeRequest.getCodeChallengeMethod());
            }
            AuthorizeConsent consent = AuthorizeConsent.builder()
                    .clientId(authorizeRequest.getClientId())
                    .scope(authorizeRequest.getScope())
                    .user(authorizeRequest.getUser())
                    .userAttributes(authorizeRequest.getUserAttributes())
                    .build();
            // 映射code->consent，用于后续token接口校验
            authorizationConsentManager.setConsent(code, consent);
            String callbackUri = authorizeRequest.getRedirectUri() + "?code=" + code;
            if (authorizeRequest.getState() != null) {
                callbackUri += ("&state=" + authorizeRequest.getState());
            }
            return callbackUri;
        } else {
            log.error("use rejected authorization request for client_id:{}", authorizeRequest.getClientId());
            String callbackUri = authorizeRequest.getRedirectUri() + "?error=" + "user_rejected";
            if (authorizeRequest.getState() != null) {
                callbackUri += ("&state=" + authorizeRequest.getState());
            }
            return callbackUri;
        }
    }


    public AccessToken accessTokenRequest(AccessTokenRequest accessTokenRequest) {
        checkTokenRequestParam(accessTokenRequest);
        for (TokenRequestHandler tokenRequestHandler : tokenRequestHandlers) {
            if (tokenRequestHandler.supports(accessTokenRequest.getGrantType())) {
                return tokenRequestHandler.handleRequest(accessTokenRequest);
            }
        }
        throw new IllegalArgumentException("unsupported grant_type: " + accessTokenRequest.getGrantType());
    }


    private void checkAuthorizeRequestParam(AuthorizeRequest authorizeRequest) {
        if (!OAuth20GrantType.RESPONSE_TYPE_CODE.equals(authorizeRequest.getResponseType())) {
            throw new IllegalArgumentException("Invalid response_type: " + authorizeRequest.getResponseType());
        }
        AuthClient authClient = authClientStore.findByClientId(authorizeRequest.getClientId());
        if (authClient == null) {
            throw new IllegalArgumentException("Invalid client_id: " + authorizeRequest.getClientId());
        }
        if (!Objects.equals(authClient.getRedirectUri(), authorizeRequest.getRedirectUri())) {
            throw new IllegalArgumentException("Invalid redirect_uri: " + authorizeRequest.getRedirectUri());
        }
        if (authorizeRequest.getCodeChallenge() != null) {
            String challengeMethod = authorizeRequest.getCodeChallengeMethod();
            if (challengeMethod != null) {
                if (OAuth20Constant.PKCE_METHOD_PLAIN.equals(challengeMethod)) {
                    return;
                }
                if (OAuth20Constant.PKCE_METHOD_S256.equals(challengeMethod)) {
                    return;
                }
                throw new IllegalArgumentException("unsupported challenge method: " + authorizeRequest.getCodeChallengeMethod());
            }
        }

    }

    private void checkTokenRequestParam(AccessTokenRequest tokenRequest) {
        AuthClient authClient = authClientStore.findByClientId(tokenRequest.getClientId());
        if (authClient == null) {
            throw new IllegalArgumentException("Invalid client_id: " + tokenRequest.getClientId());
        }
        if (!Objects.equals(authClient.getClientSecret(), tokenRequest.getClientSecret())) {
            throw new IllegalArgumentException("Invalid client_secret: " + tokenRequest.getClientSecret());
        }

        // authorization_code授权
        if (OAuth20GrantType.GRANT_TYPE_AUTHORIZATION_CODE.equals(tokenRequest.getGrantType())) {
            if (!Objects.equals(authClient.getRedirectUri(), tokenRequest.getRedirectUri())) {
                throw new IllegalArgumentException("Invalid redirect_uri: " + tokenRequest.getRedirectUri());
            }
            if (!authorizationCodeManager.checkCode(tokenRequest.getClientId(), tokenRequest.getCode())) {
                throw new IllegalArgumentException("Invalid authorization_code: " + tokenRequest.getCode());
            }
            if (tokenRequest.getCodeVerifier() != null) {
                if (!authorizationCodeManager.checkCodeVerifier(tokenRequest.getCode(), tokenRequest.getCodeVerifier())) {
                    throw new IllegalArgumentException("Invalid code_verifier: " + tokenRequest.getCodeVerifier());
                }
            }
            return;
        }

        // password授权
        if (OAuth20GrantType.GRANT_TYPE_RESOURCE_OWNER.equals(tokenRequest.getGrantType())) {
            return;
        }

        // client_credentials授权
        if (OAuth20GrantType.GRANT_TYPE_CLIENT_CREDENTIALS.equals(tokenRequest.getGrantType())) {
            return;
        }
        throw new IllegalArgumentException("unsupported grant_type: " + tokenRequest.getGrantType());

    }


    public UserProfile userProfile(String accessToken) {
        return tokenStorage.getUserProfileByToken(accessToken);
    }
}
