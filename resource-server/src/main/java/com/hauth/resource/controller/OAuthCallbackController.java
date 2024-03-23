package com.hauth.resource.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hauth.resource.config.OAuthProperties;
import com.hauth.resource.model.AccessTokenDTO;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/7 23:20
 */
@RestController
public class OAuthCallbackController {


    @Autowired
    private OAuthProperties oAuthProperties;

    WebClient webClient = WebClient.create("http://127.0.0.1:8000");

    private final Logger logger = LoggerFactory.getLogger(OAuthCallbackController.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    private void init() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    @CrossOrigin
    @GetMapping("/")
    public Mono<String> authorized(@RequestParam(value = "code", required = false) String code,
                                   @RequestParam(value = "error", required = false) String error,
                                   @RequestParam(value = "state", required = false) String state,
                                   HttpServletRequest request) {
        logger.info("authorized callback, code={}, state={}", code, state);
        if (code == null) {
            return Mono.just("Server Error: " + error);
        }
        if (state != null) {
            Object stateVal = request.getSession(true).getAttribute(oAuthProperties.getStateName());
            if (!Objects.equals(state, stateVal)) {
                logger.error("state is recognized: {}", state);
                return Mono.just("state is recognized:" + state);
            }
        }
        String clientId = oAuthProperties.getClientId();
        String clientSecret = oAuthProperties.getClientSecret();
        String base64Credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", oAuthProperties.getRedirectUri());
        formData.add("code", code);
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        String codeVerifier = (String) request.getSession().getAttribute(oAuthProperties.getCodeVerifierName());
        return webClient.post()
                .uri("/oauth2.0/token" + "?code_verifier=" + codeVerifier)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + base64Credentials)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(throwable -> {
                    logger.error("request error: {}", throwable.getMessage());
                })
                .onErrorReturn("OAuth Server Error")
                .flatMap(json -> {
                    try {
                        logger.info("token str: {}", json);
                        // 解析出access_token并请求/userinfo接口获取信息
                        AccessTokenDTO accessTokenDTO = objectMapper.readValue(json, AccessTokenDTO.class);
                        String accessToken = accessTokenDTO.getAccessToken();
                        String uri = "/oauth2.0/profile?access_token=" + accessToken;
//                        parseClaims(accessToken);
                        return webClient.get().uri(uri)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .retrieve()
                                .bodyToMono(String.class)
                                .doOnSuccess(str -> {
                                    request.getSession(false).setAttribute(oAuthProperties.getAuthName(), str);
                                });

                    } catch (JsonProcessingException e) {
                        return Mono.just("error");
                    }
                });

    }

    private String parseClaims(String accessToken) {
        String payload = accessToken.split("\\.")[1];
        byte[] decode = Base64.getUrlDecoder().decode(payload);
        payload = new String(decode);
        logger.info("payload: {}", payload);
        return payload;
    }

}
