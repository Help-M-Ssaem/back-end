package com.example.mssaem_backend.global.config.security.oauth;

import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.AuthErrorCode;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class SocialLoginService {

    @Value("${social.grant-type}")
    private String grantType;
    @Value("${social.kakao.client-id}")
    private String kakaoClientId;
    @Value("${social.kakao.redirect-uri}")
    private String kakaoRedirectUrl;

    @Value("${social.google.client-id}")
    private String googleClientId;
    @Value("${social.google.client-secret}")
    private String googleClientSecret;
    @Value("${social.google.redirect-uri}")
    private String googleRedirectUrl;

    @Value("${social.naver.client-id}")
    private String naverClientId;
    @Value("${social.naver.redirect-uri}")
    private String naverRedirectUrl;
    @Value("${social.naver.client-secret}")
    private String naverClientSecret;

    public String getGoogleAccessToken(String idToken) throws IOException {
        String reqUrl = "https://oauth2.googleapis.com/token";
        String parameter = "grant_type=" + grantType +
                "&client_id=" + googleClientId +            // REST_API_KEY
                "&client_secret=" + googleClientSecret +    // SECRET_KEY
                "&redirect_uri=" + googleRedirectUrl +      // REDIRECT_URI
                "&code=" + java.net.URLDecoder.decode(idToken, StandardCharsets.UTF_8);
        return getAccessToken(idToken, reqUrl, parameter);
    }

    public String TestGetGoogleAccessToken(String idToken) throws IOException {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://oauth2.googleapis.com/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        JSONObject response = webClient.post()
                //.uri("token")
                .body(BodyInserters
                        .fromFormData("client_id", googleClientId)
                                .with("client_secret", googleClientSecret)
                                .with("grant_type", grantType)
                                .with("redirect_uri", googleRedirectUrl)
                                .with("code", java.net.URLDecoder.decode(idToken, StandardCharsets.UTF_8)))
                .retrieve()
                .bodyToFlux(JSONObject.class)
                //.onErrorMap(e -> new BaseException(AuthErrorCode.INVALID_ID_TOKEN))
                .blockLast();

        return (String) response.get("access_token");
    }
    public String getKaKaoAccessToken(String idToken) throws IOException {
        String reqUrl = "https://kauth.kakao.com/oauth/token";
        String parameter = "grant_type=" + grantType +
                "&client_id=" + kakaoClientId + // REST_API_KEY
                "&redirect_uri=" + kakaoRedirectUrl + // REDIRECT_URI
                "&code=" + idToken;
        return getAccessToken(idToken, reqUrl, parameter);
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
                .retrieve().bodyToMono(JSONObject.class).block();

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


    public String getAccessToken(String idToken, String requestUrl, String parameter) throws IOException {
        String accessToken = "";

        URL url = new URL(requestUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod(HttpMethod.POST.name());
        conn.setDoOutput(true);

        // POST 요청에서 필요한 파라미터를 OutputStream을 통해 전송
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(parameter);
        bw.flush();


        if (conn.getResponseCode() >= 400) {
            throw new BaseException(AuthErrorCode.INVALID_ID_TOKEN);
        }

        // 요청을 통해 얻은 데이터를 InputStreamReader을 통해 읽어 오기
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = "";
        StringBuilder result = new StringBuilder();

        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        JsonElement element = new JsonParser().parse(result.toString());

        accessToken = element.getAsJsonObject().get("access_token").getAsString();

        br.close();
        bw.close();

        return accessToken;
    }

    public String getKaKaoEmail(String accessToken) throws IOException {
        String requestUrl = "https://kapi.kakao.com/v2/user/me";
        StringBuilder result = getEmail(accessToken, requestUrl);
        return new JsonParser().parse(result.toString()).getAsJsonObject().get("kakao_account")
                .getAsJsonObject().get("email").getAsString();
    }

    public String getGoogleEmail(String accessToken) throws IOException {
        // String requestUrl = "https://oauth2.googleapis.com/token";

        String requestUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
        StringBuilder result = getEmail(accessToken, requestUrl);
        return new JsonParser().parse(result.toString()).getAsJsonObject().get("email").getAsString();
    }

    public StringBuilder getEmail(String accessToken, String requestUrl) throws IOException {
        URL url = new URL(requestUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod(HttpMethod.GET.name());
        conn.setRequestProperty("Authorization", " Bearer " + accessToken);

        if (conn.getResponseCode() >= 400) {
            throw new BaseException(AuthErrorCode.INVALID_ACCESS_TOKEN);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        br.close();
        return result;
    }
}
