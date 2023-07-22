package com.example.mssaem_backend.domain.member.dto;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponseDto {

    @Getter
    @AllArgsConstructor
    public static class TokenInfo {

        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberSimpleInfo {

        private Long id;
        private String nickName;
        private MbtiEnum mbtiEnum;
        private String badge;
        private String profileImgUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class CheckNickNameRes {

        private boolean isUsed;
    }

    @Getter
    @AllArgsConstructor
    public static class TeacherInfo {

        private Long id;
        private String nickName;
        private MbtiEnum mbtiEnum;
        private String badge;
        private String profileImgUrl;
        private String introduction;
    }
}