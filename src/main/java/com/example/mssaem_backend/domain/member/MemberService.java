package com.example.mssaem_backend.domain.member;

import com.example.mssaem_backend.domain.member.dto.MemberRequestDto.RegisterMember;
import com.example.mssaem_backend.domain.member.dto.MemberRequestDto.CheckNickName;
import com.example.mssaem_backend.domain.member.dto.MemberRequestDto.SocialLoginToken;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.TokenInfo;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.MemberErrorCode;
import com.example.mssaem_backend.global.config.security.jwt.JwtTokenProvider;
import com.example.mssaem_backend.global.config.security.oauth.KakaoLoginService;
import com.example.mssaem_backend.global.config.security.oauth.SocialLoginType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final KakaoLoginService kakaoLoginService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public void save(Member member) {
        memberRepository.save(member);
    }

    public Member register(RegisterMember registerMember) {
        Member member = Member.builder()
                .email(registerMember.getEmail())
                .nickName(registerMember.getNickname())
                .mbti(registerMember.getMbti())
                .refreshToken("")
                .role(Role.ROLE_MEMBER)
                .build();
        save(member);
        return member;
    }

    public TokenInfo registerMember(RegisterMember registerMember) {
        checkRegister(registerMember);
        Member member = register(registerMember);
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(member.getId());
        member.changeRefreshToken(tokenInfo.getRefreshToken());
        return tokenInfo;
    }

    public TokenInfo socialLogin(SocialLoginType socialLoginType,
                                 SocialLoginToken socialLoginToken) throws BaseException, IOException {
        String idToken = socialLoginToken.getIdToken();
        String email = "";
        switch (socialLoginType) {
            case KAKAO -> email = kakaoLoginService.getEmail(idToken);
            //case GOOGLE -> email =
            //case NAVER -> email =
        }

        Member member = memberRepository.findByEmail(email).orElse(null);
        if (member != null) {
            TokenInfo tokenInfo = jwtTokenProvider.generateToken(member.getId());
            member.changeRefreshToken(tokenInfo.getRefreshToken());
            return tokenInfo;
        } else {
            BaseException exception = new BaseException(MemberErrorCode.UN_REGISTERED_MEMBER);
            exception.setEmailMessage(email);
            throw exception;
        }
    }

    public void checkRegister(RegisterMember registerMember) {
        Boolean flag = memberRepository.existsByEmail(registerMember.getEmail());
        if (flag) {
            throw new BaseException(MemberErrorCode.DUPLICATE_MEMBER);
        }
        flag = memberRepository.existsByNickName(registerMember.getNickname());
        if (flag) {
            throw new BaseException(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }
}
