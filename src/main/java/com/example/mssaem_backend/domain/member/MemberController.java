package com.example.mssaem_backend.domain.member;

import com.example.mssaem_backend.domain.member.dto.MemberRequestDto.ModifyProfile;
import com.example.mssaem_backend.domain.member.dto.MemberRequestDto.CheckNickName;
import com.example.mssaem_backend.domain.member.dto.MemberRequestDto.RegisterMember;
import com.example.mssaem_backend.domain.member.dto.MemberRequestDto.SocialLoginToken;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberProfileInfo;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.CheckNickNameRes;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.TeacherInfo;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.TokenInfo;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
import com.example.mssaem_backend.global.config.security.oauth.SocialLoginType;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 홈 화면 조회 - 인기 M쌤
     */
    @GetMapping("/teacher")
    public ResponseEntity<List<TeacherInfo>> findHotTeacherForHome() {
        return ResponseEntity.ok(memberService.findHotTeacherForHome());
    }

    /**
     * [PATCH] 프로필 수정
     */
    @PatchMapping("/member/profile")
    public ResponseEntity<String> modifyProfile(
            @CurrentMember Member member, @RequestPart(value = "modifyProfile") ModifyProfile modifyProfile,
            @RequestPart(value = "image", required = false) List<MultipartFile> multipartFile) {
        return new ResponseEntity<>(memberService.modifyProfile(member, modifyProfile, multipartFile), HttpStatus.OK);
    }

    /**
     * [GET] 프로필 조회
     */
    @GetMapping("/profile/{id}")
    public ResponseEntity<MemberProfileInfo> getProfile(
            @PathVariable("id") Long memberId) {
        return new ResponseEntity<>(memberService.getProfile(memberId), HttpStatus.OK);
    }


}
