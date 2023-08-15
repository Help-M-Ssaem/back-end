package com.example.mssaem_backend.global.config.security.oauth;

import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.AuthErrorCode;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Map;

@Service
public class NaverSocialLoginService {

    @Value("${social.grant-type}")
    private String grantType;
    @Value("${social.naver.client-id}")
    private String naverClientId;
    @Value("${social.naver.client-secret}")
    private String naverClientSecret;

    public String naverSocialLogin(String idToken) throws IOException {
        return getNaverEmail(getNaverAccessToken(idToken));
    }

    public String getNaverAccessToken(String idToken) throws IOException {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://nid.naver.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        JSONObject response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth2.0/token")
                        .queryParam("client_id", naverClientId)
                        .queryParam("client_secret", naverClientSecret)
                        .queryParam("grant_type", grantType)
                        .queryParam("state", "test")
                        .queryParam("code", idToken)
                        .build())
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();
        if (response == null) {
            throw new BaseException(AuthErrorCode.INVALID_ID_TOKEN);
        }

        return (String) response.get("access_token");
    }

    public String getNaverEmail(String accessToken) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://openapi.naver.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        JSONObject response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/nid/me")
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve().bodyToMono(JSONObject.class).block();

        if (response == null) {
            throw new BaseException(AuthErrorCode.INVALID_ACCESS_TOKEN);
        }

        Map<String, Object> res = (Map<String, Object>) response.get("response");
        return (String) res.get("email");
    }
}
