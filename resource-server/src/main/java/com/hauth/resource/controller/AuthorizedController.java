package com.hauth.resource.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hauth.resource.model.AccessTokenDTO;
import jakarta.annotation.PostConstruct;
import org.apereo.cas.client.util.AssertionHolder;
import org.apereo.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/7 23:20
 */
@RestController
public class AuthorizedController {


    WebClient webClient = WebClient.create("http://127.0.0.1:8000");

    private final Logger logger = LoggerFactory.getLogger(AuthorizedController.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    private void init() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    @GetMapping("/")
    public Mono<String> index() {
        Assertion assertion = AssertionHolder.getAssertion();
        String user = assertion.getPrincipal().getName();
        Map<String, Object> attributes = assertion.getPrincipal().getAttributes();
        logger.info("user:{} login, attributes:{}", user, attributes);
        return Mono.just("Hello, " + user + "!\n" + attributes);
    }

    @GetMapping("/authorized")
    public Mono<String> authorized(@RequestParam("code") String code) {
        String clientId = "hello";
        String clientSecret = "123456";
        String base64Credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", "http://127.0.0.1:8080/authorized");
        formData.add("code", code);
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);

        return webClient.post()
                .uri("/oauth2/token")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + base64Credentials)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(json -> {
                    try {
                        logger.info("token str: {}", json);
                        // 解析出access_token并请求/userinfo接口获取信息
                        AccessTokenDTO accessTokenDTO = objectMapper.readValue(json, AccessTokenDTO.class);
                        String accessToken = accessTokenDTO.getAccessToken();
                        parseClaims(accessToken);
                        return webClient.get().uri("/userinfo")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .retrieve()
                                .bodyToMono(String.class);

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
