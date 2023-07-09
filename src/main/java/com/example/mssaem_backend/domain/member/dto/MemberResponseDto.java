package com.example.mssaem_backend.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class MemberResponseDto {

    @Getter
    @AllArgsConstructor
    public static class TokenInfo {
        private String accessToken;
        private String refreshToken;
    }
}
