package com.hauth.cas.oauth2;

import com.hauth.cas.auth.Authentication;
import com.hauth.cas.auth.AuthenticationManager;
import com.hauth.cas.oauth2.config.OAuth20Constant;
import com.hauth.cas.oauth2.config.OAuth20Properties;
import com.hauth.cas.oauth2.dto.AccessTokenRequest;
import com.hauth.cas.oauth2.dto.AccessToken;
import com.hauth.cas.oauth2.dto.AuthorizeRequest;
import com.hauth.cas.oauth2.dto.UserProfile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/18 17:33
 */
@Slf4j
@RestController
@RequestMapping("/oauth2.0")
public class OAuth20Controller {

    @Autowired
    private OAuth20Service oAuth20Service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private OAuth20Properties oAuth20Properties;


    @GetMapping("/authorize")
    public String authorize(@RequestParam("client_id") String clientId,
                            @RequestParam("response_type") String responseType,
                            @RequestParam("redirect_uri") String redirectUri,
                            @RequestParam(value = "code_challenge", required = false) String codeChallenge,
                            @RequestParam(value = "code_challenge_method", required = false) String codeChallengeMethod,
                            @RequestParam(value = "state", required = false) String state,
                            HttpServletRequest request,
                            HttpServletResponse response) throws IOException {

        AuthorizeRequest authorizeRequest = AuthorizeRequest.builder()
                .clientId(clientId)
                .responseType(responseType)
                .redirectUri(redirectUri)
                .codeChallenge(codeChallenge)
                .codeChallengeMethod(codeChallengeMethod)
                .state(state)
                .request(request)
                .response(response)
                .build();
        // 如果未登录，则重定向到登录页面
        if (!authenticationManager.hasLogin(request)) {
            log.warn("unauthenticated user, redirect to login page");
            redirectToLogin(request, response);
            return "authenticate";
        }

        // 获取用户ID信息
        Authentication authentication = authenticationManager.getUserAuthentication(request);
        authorizeRequest.setUser(authentication.getPrincipal());
        authorizeRequest.setUserAttributes(authentication.getAttributes());

        // 检查是否已经授权
        if (oAuth20Service.hasConsent(authorizeRequest)) {
            authorizeRequest.setConsent(AuthorizationConsentManager.CONSENT_APPROVED);
            String callbackUrl = oAuth20Service.authorizationConsent(authorizeRequest);
            log.info("redirect to callback uri: {}", callbackUrl);
            response.sendRedirect(callbackUrl);
            return "redirect";
        } else {
            log.info("user has not approve authorization, send redirect to user consent page");
            // 保存authorizeRequest对象到session中
            oAuth20Service.checkAuthorizeRequest(authorizeRequest);
            renderConsentPage(response, authorizeRequest);
            return "consent";
        }


    }

    @PostMapping("/consent")
    public String authorizeConsent(@RequestParam("consent") String consent,
                                   @RequestParam(value = "scope", required = false) String scope,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws IOException {
        log.info("user consent result, consent={}, scope={}", consent, scope);
        AuthorizeRequest authorizationRequest = oAuth20Service.getAuthorizationRequest(request);
        authorizationRequest.setConsent(consent);
        authorizationRequest.setScope(scope);
        String callbackUri = oAuth20Service.authorizationConsent(authorizationRequest);
        log.info("user consent, redirect to client serviceUrl: {}", callbackUri);
        response.sendRedirect(callbackUri);
        return "redirect";
    }


    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String callbackUri = oAuth20Properties.getOauthServer() + oAuth20Properties.getAuthorizeEndpoint() + "?" + request.getQueryString();
        String encodeUrl = URLEncoder.encode(callbackUri, StandardCharsets.UTF_8.toString());
        String redirectUri = oAuth20Properties.getAuthServer() + oAuth20Properties.getAuthLoginUri() + "?service=" + encodeUrl;
        log.info("redirect to login: {}", redirectUri);
        response.sendRedirect(redirectUri);

    }

    private void renderConsentPage(HttpServletResponse response,
                                   AuthorizeRequest authorizeRequest) throws IOException {

        ClassPathResource resource = new ClassPathResource("static/consent.html");

        log.info("render consent page: {}", resource.getPath());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        try (InputStream inputStream = resource.getInputStream();
             OutputStream outputStream = response.getOutputStream()) {
            byte[] bytes = new byte[inputStream.available()];
            IOUtils.readFully(inputStream, bytes);
            String consentPage = new String(bytes, StandardCharsets.UTF_8);
            consentPage = consentPage.replace(OAuth20Constant.CLIENT_NAME, authorizeRequest.getClientName());
            consentPage = consentPage.replace(OAuth20Constant.CLIENT_URL, authorizeRequest.getRedirectUri());
            IOUtils.copy(new ByteArrayInputStream(consentPage.getBytes(StandardCharsets.UTF_8)), outputStream);
        }

    }


    @PostMapping(path = {"/accessToken", "/token"},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public AccessToken accessToken(@RequestParam("grant_type") String grantType,
                                   @RequestParam("client_id") String clientId,
                                   @RequestParam("client_secret") String clientSecret,
                                   @RequestParam(value = "redirect_uri", required = false) String redirectUri,
                                   @RequestParam(value = "code", required = false) String code,
                                   @RequestParam(value = "code_verifier", required = false) String codeVerifier,
                                   @RequestParam(value = "username", required = false) String username,
                                   @RequestParam(value = "password", required = false) String password) {
        AccessTokenRequest accessTokenRequest = AccessTokenRequest.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(grantType)
                .redirectUri(redirectUri)
                .code(code)
                .codeVerifier(codeVerifier)
                .username(username)
                .password(password)
                .build();
        log.info("accessToken request: {}", accessTokenRequest);
        return oAuth20Service.accessTokenRequest(accessTokenRequest);
    }

    @GetMapping("/profile")
    public UserProfile userProfile(@RequestParam("access_token") String accessToken) {
        log.info("user profile request, token: {}", accessToken);
        UserProfile userProfile = oAuth20Service.userProfile(accessToken);
        if (userProfile == null) {
            throw new IllegalArgumentException("Invalid access_token:" + accessToken);
        }
        return userProfile;
    }


    @PostMapping("/introspect")
    public String introspect() {
        // TODO 检查accessToken的状态
        return null;
    }


    @PostMapping("/device")
    public String device() {
        // TODO device flow
        return null;
    }

    @PostMapping("/revoke")
    public String revokeToken() {
        // TODO 撤销accessToken或者refreshToken
        return null;
    }

}
