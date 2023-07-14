package com.example.mssaem_backend.domain.member;

import com.example.mssaem_backend.domain.member.dto.MemberRequestDto.SocialLoginToken;
import com.example.mssaem_backend.domain.member.dto.MemberRequestDto.RegisterMember;
import com.example.mssaem_backend.domain.member.dto.MemberRequestDto.CheckNickName;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.CheckNickNameRes;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.TokenInfo;
import com.example.mssaem_backend.global.config.security.oauth.SocialLoginType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberService memberService;

    /**
     * [POST] 소셜 회원가입
     */
    @PostMapping("/sign-up")
    public ResponseEntity<TokenInfo> register(
            @Valid @RequestBody RegisterMember registerMember) {
        return new ResponseEntity<>
                (memberService.registerMember(registerMember), HttpStatus.OK);
    }

    /**
     * [POST] 소셜 로그인
     */
    @PostMapping("/{socialLoginType}/login")
    public ResponseEntity<TokenInfo> socialLogin(
            @PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
            @Valid @RequestBody SocialLoginToken socialLoginToken) throws IOException {
        return new ResponseEntity<>(
                memberService.socialLogin(socialLoginType, socialLoginToken), HttpStatus.OK);
    }

    /**
     * [POST] 닉네임 중복 확인
     */
    @PostMapping("/nick-name")
    public ResponseEntity<CheckNickNameRes> checkNickName(
            @Valid @RequestBody CheckNickName checkNickName) {
        return new ResponseEntity<>(memberService.checkNickName(checkNickName),
                HttpStatus.OK);
    }


}
