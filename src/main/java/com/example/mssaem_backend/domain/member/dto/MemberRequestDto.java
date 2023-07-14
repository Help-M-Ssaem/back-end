package com.example.mssaem_backend.domain.member.dto;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
        //private boolean option1;
        //private boolean option2;
        //private boolean option3;
        //private boolean option4;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CheckNickName {
        @NotBlank(message = "닉네임은 공백일 수 없습니다.")
        private String nickName;
    }
}
