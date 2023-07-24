package com.example.mssaem_backend.global.config.security.oauth;

import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.AuthErrorCode;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

@Service
public class KakaoLoginService {

    public String getAccessToken(String idToken) throws IOException {
        String accessToken = "";
        String reqUrl = "https://kauth.kakao.com/oauth/token";

        URL url = new URL(reqUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod(HttpMethod.POST.name());
        conn.setDoOutput(true);

        // POST 요청에서 필요한 파라미터를 OutputStream을 통해 전송
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        String sb = "grant_type=authorization_code" +
                "&client_id=0b193a8cee2b14d1a2c57470cb2d8e3b" + // REST_API_KEY
                "&redirect_uri=http://localhost:3000/kakao/login" + // REDIRECT_URI
                "&code=" + idToken;
        bufferedWriter.write(sb);
        bufferedWriter.flush();

        if (conn.getResponseCode() >= 400) {
            throw new BaseException(AuthErrorCode.INVALID_ID_TOKEN);
        }

        // 요청을 통해 얻은 데이터를 InputStreamReader을 통해 읽어 오기
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = "";
        StringBuilder result = new StringBuilder();

        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }
        System.out.println("response body : " + result);

        JsonElement element = new JsonParser().parse(result.toString());

        accessToken = element.getAsJsonObject().get("access_token").getAsString();

        bufferedReader.close();
        bufferedWriter.close();

        System.out.println("accessToken : " + accessToken);
        return accessToken;
    }

    public String getEmail(String accessToken) throws IOException {
        String reqUrl = "https://kapi.kakao.com/v2/user/me";

        URL url = new URL(reqUrl);
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

        return new JsonParser().parse(result.toString()).getAsJsonObject().get("kakao_account")
                .getAsJsonObject().getAsJsonObject().get("email").getAsString();
    }
}
