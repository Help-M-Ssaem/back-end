package com.example.mssaem_backend.global.config.security.oauth;

import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.AuthErrorCode;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class GoogleSocialLoginService extends SocialLoginService {

    @Value("${social.grant-type}")
    private String grantType;
    @Value("${social.google.client-id}")
    private String googleClientId;
    @Value("${social.google.client-secret}")
    private String googleClientSecret;
    @Value("${social.google.redirect-uri}")
    private String googleRedirectUrl;

    public String googleSocialLogin(String idToken) throws IOException {
        return getGoogleEmail(getGoogleAccessToken(idToken));
    }

    public String getGoogleAccessToken(String idToken) throws IOException {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://oauth2.googleapis.com/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        JSONObject response = webClient.post()
                .body(BodyInserters
                        .fromFormData("client_id", googleClientId)
                        .with("client_secret", googleClientSecret)
                        .with("grant_type", grantType)
                        .with("redirect_uri", googleRedirectUrl)
                        .with("code", java.net.URLDecoder.decode(idToken, StandardCharsets.UTF_8)))
                .retrieve()
                .bodyToFlux(JSONObject.class)
                .blockLast();

        if (response == null) {
            throw new BaseException(AuthErrorCode.INVALID_ID_TOKEN);
        }
        return (String) response.get("access_token");
    }

    public String getGoogleEmail(String accessToken) throws IOException {
        String requestUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
        StringBuilder result = getEmail(accessToken, requestUrl);
        return new JsonParser().parse(result.toString()).getAsJsonObject().get("email").getAsString();
    }
}
