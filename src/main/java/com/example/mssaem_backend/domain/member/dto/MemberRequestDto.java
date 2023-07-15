package com.example.mssaem_backend.domain.member.dto;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SocialLoginToken {
        @NotBlank(message = "ID Token이 없습니다.")
        private String idToken;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterMember {
        @NotBlank(message = "이메일 형식이 아닙니다.")
        @Email(message = "이메일 형식이 아닙니다.")
        private String email;
        @NotBlank(message = "닉네임은 공백일 수 없습니다.")
        private String nickname;
        private MbtiEnum mbti;
        @NotBlank(message = "mbti를 골라주세요.")
        private String caseSensitivity; //대소문자 구분
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CheckNickName {
        @NotBlank(message = "닉네임은 공백일 수 없습니다.")
        private String nickName;
    }
}
