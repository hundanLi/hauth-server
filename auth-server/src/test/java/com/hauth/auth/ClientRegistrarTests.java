package com.hauth.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/12 10:49
 */
public class ClientRegistrarTests {

    private final WebClient webClient = WebClient.create("http://127.0.0.1:8000");

    static class ClientRegistrationRequest {

        @JsonProperty("client_name")
        String clientName;
        @JsonProperty("grant_types")
        List<String> grantTypes;
        @JsonProperty("redirect_uris")
        List<String> redirectUris;
        @JsonProperty("logo_uri")
        String logoUri;
        @JsonProperty("contacts")
        List<String> contacts;
        @JsonProperty("scope")
        String scope;

        public ClientRegistrationRequest(String clientName, List<String> grantTypes, List<String> redirectUris, String logoUri, List<String> contacts, String scope) {
            this.clientName = clientName;
            this.grantTypes = grantTypes;
            this.redirectUris = redirectUris;
            this.logoUri = logoUri;
            this.contacts = contacts;
            this.scope = scope;
        }
    }


    static class ClientRegistrationResponse {

        @JsonProperty("registration_access_token")
        String registrationAccessToken;
        @JsonProperty("registration_client_uri")
        String registrationClientUri;
        @JsonProperty("client_name")
        String clientName;
        @JsonProperty("client_id")
        String clientId;
        @JsonProperty("client_secret")
        String clientSecret;
        @JsonProperty("grant_types")
        List<String> grantTypes;
        @JsonProperty("redirect_uris")
        List<String> redirectUris;
        @JsonProperty("logo_uri")
        String logoUri;
        @JsonProperty("contacts")
        List<String> contacts;
        @JsonProperty("scope")
        String scope;

        public ClientRegistrationResponse(String registrationAccessToken, String registrationClientUri, String clientName, String clientId, String clientSecret, List<String> grantTypes, List<String> redirectUris, String logoUri, List<String> contacts, String scope) {
            this.registrationAccessToken = registrationAccessToken;
            this.registrationClientUri = registrationClientUri;
            this.clientName = clientName;
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.grantTypes = grantTypes;
            this.redirectUris = redirectUris;
            this.logoUri = logoUri;
            this.contacts = contacts;
            this.scope = scope;
        }
    }

    static class AccessToken {
        @JsonProperty("access_token")
        String accessToken;
        @JsonProperty("token_type")
        String tokenType;
        @JsonProperty("expires_in")
        Integer expiresIn;
        @JsonProperty("scope")
        String scope;

        public AccessToken(String accessToken, String tokenType, Integer expiresIn, String scope) {
            this.accessToken = accessToken;
            this.tokenType = tokenType;
            this.expiresIn = expiresIn;
            this.scope = scope;
        }
    }

    @Test
    public void exampleRegistration() {

        String clientId = "registrar";
        String clientSecret = "123456";
        MultiValueMap<String, String> formMap = new LinkedMultiValueMap<>();
        formMap.add("grant_type", "client_credentials");
        formMap.add("scope", "client.create");

        String basicAuth = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
        // 获取access_token
        AccessToken accessToken = webClient.post().uri("/oauth2/token")
                .header("Authorization", "Basic " + basicAuth)
                .header("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE)
                .body(BodyInserters.fromFormData(formMap))
                .retrieve()
                .bodyToMono(AccessToken.class)
                .block();

        assert accessToken != null;
        String initialAccessToken = accessToken.accessToken;


//        (3)
        ClientRegistrationRequest clientRegistrationRequest = new ClientRegistrationRequest( // (4)
                "client-1",
                Collections.singletonList(AuthorizationGrantType.AUTHORIZATION_CODE.getValue()),
                Arrays.asList("https://client.example.org/callback", "https://client.example.org/callback2"),
                "https://client.example.org/logo",
                Arrays.asList("contact-1", "contact-2"),
                "openid email profile"
        );

        ClientRegistrationResponse clientRegistrationResponse =
                registerClient(initialAccessToken, clientRegistrationRequest);
//        (5)

        assert (clientRegistrationResponse.clientName.contentEquals("client-1"));
//        (6)
        assert (!Objects.isNull(clientRegistrationResponse.clientSecret));
        assert (clientRegistrationResponse.scope.contains("openid"));
        assert (clientRegistrationResponse.scope.contains("profile"));
        assert (clientRegistrationResponse.scope.contains("email"));
        assert (clientRegistrationResponse.grantTypes.contains(AuthorizationGrantType.AUTHORIZATION_CODE.getValue()));
        assert (clientRegistrationResponse.redirectUris.contains("https://client.example.org/callback"));
        assert (clientRegistrationResponse.redirectUris.contains("https://client.example.org/callback2"));
        assert (!clientRegistrationResponse.registrationAccessToken.isEmpty());
        assert (!clientRegistrationResponse.registrationClientUri.isEmpty());
        assert (clientRegistrationResponse.logoUri.contentEquals("https://client.example.org/logo"));
        assert (clientRegistrationResponse.contacts.size() == 2);
        assert (clientRegistrationResponse.contacts.contains("contact-1"));
        assert (clientRegistrationResponse.contacts.contains("contact-2"));

        String registrationAccessToken = clientRegistrationResponse.registrationAccessToken;
//        (7)
        String registrationClientUri = clientRegistrationResponse.registrationClientUri;

        ClientRegistrationResponse retrievedClient = retrieveClient(registrationAccessToken, registrationClientUri);
//        (8)

        assert (retrievedClient.clientName.contentEquals("client-1"));
//        (9)
        assert (!Objects.isNull(retrievedClient.clientId));
        assert (!Objects.isNull(retrievedClient.clientSecret));
        assert (clientRegistrationResponse.scope.contains("openid"));
        assert (clientRegistrationResponse.scope.contains("profile"));
        assert (clientRegistrationResponse.scope.contains("email"));
        assert (retrievedClient.grantTypes.contains(AuthorizationGrantType.AUTHORIZATION_CODE.getValue()));
        assert (retrievedClient.redirectUris.contains("https://client.example.org/callback"));
        assert (retrievedClient.redirectUris.contains("https://client.example.org/callback2"));
        assert (retrievedClient.logoUri.contentEquals("https://client.example.org/logo"));
        assert (retrievedClient.contacts.size() == 2);
        assert (retrievedClient.contacts.contains("contact-1"));
        assert (retrievedClient.contacts.contains("contact-2"));
        assert (Objects.isNull(retrievedClient.registrationAccessToken));
        assert (!retrievedClient.registrationClientUri.isEmpty());
    }

    public ClientRegistrationResponse registerClient(String initialAccessToken, ClientRegistrationRequest request) {
//        (10)
        return this.webClient
                .post()
                .uri("/connect/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + initialAccessToken)
                .body(Mono.just(request), ClientRegistrationRequest.class)
                .retrieve()
                .bodyToMono(ClientRegistrationResponse.class)
                .block();
    }

    public ClientRegistrationResponse retrieveClient(String registrationAccessToken, String registrationClientUri) {
//        (11)
        return this.webClient
                .get()
                .uri(registrationClientUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + registrationAccessToken)
                .retrieve()
                .bodyToMono(ClientRegistrationResponse.class)
                .block();
    }

}
