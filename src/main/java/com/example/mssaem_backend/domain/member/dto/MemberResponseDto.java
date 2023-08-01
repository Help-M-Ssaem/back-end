package com.example.mssaem_backend.domain.member.dto;

import com.example.mssaem_backend.domain.member.Member;
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
        private String mbti;
        private String badge;
        private String profileImgUrl;

        public MemberSimpleInfo(Member member, String badge){
            this.id = member.getId();
            this.nickName = member.getNickName();
            this.mbti = member.getDetailMbti();
            this.badge = badge;
            this.profileImgUrl = member.getProfileImageUrl();
        }
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
        private String mbti;
        private String badge;
        private String profileImgUrl;
        private String introduction;
    }
}