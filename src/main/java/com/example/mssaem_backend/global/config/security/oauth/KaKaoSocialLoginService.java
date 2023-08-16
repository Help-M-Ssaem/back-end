package com.example.mssaem_backend.global.config.security.oauth;

import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.AuthErrorCode;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

@Service
public class KaKaoSocialLoginService extends SocialLoginService{

    @Value("${social.grant-type}")
    private String grantType;
    @Value("${social.kakao.client-id}")
    private String kakaoClientId;
    @Value("${social.kakao.redirect-uri}")
    private String kakaoRedirectUrl;

    public String kakaoSocialLogin(String idToken) throws IOException {
        return getKaKaoEmail(getKaKaoAccessToken(idToken));
    }

    public String getKaKaoAccessToken(String idToken) throws IOException {
        String reqUrl = "https://kauth.kakao.com/oauth/token";
        String parameter =
                "grant_type=" + grantType +           // 인증 타입
                "&client_id=" + kakaoClientId +       // REST_API_KEY
                "&redirect_uri=" + kakaoRedirectUrl + // REDIRECT_URI
                "&code=" + idToken;                   // 인가 코드
        return getAccessToken(reqUrl, parameter);
    }
    public String getKaKaoEmail(String accessToken) throws IOException {
        String requestUrl = "https://kapi.kakao.com/v2/user/me";
        StringBuilder result = getEmail(accessToken, requestUrl);
        return new JsonParser().parse(result.toString()).getAsJsonObject().get("kakao_account")
                .getAsJsonObject().get("email").getAsString();
    }

    /**
     * 추후 WebClient로 리팩토링을 위한 코드
     * @param accessToken
     * @return
     */
    public String TestGetKaKaoEmail(String accessToken) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://kapi.kakao.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        JSONObject response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/user/me")
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve().bodyToMono(JSONObject.class).block();

        if (response == null) {
            throw new BaseException(AuthErrorCode.INVALID_ACCESS_TOKEN);
        }

        Map<String, Object> res = (Map<String, Object>) response.get("response");

        return new JsonParser().parse(res.toString()).getAsJsonObject().get("kakao_account")
                .getAsJsonObject().get("email").getAsString();
    }
}
