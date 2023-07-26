package com.example.mssaem_backend.domain.member;

import com.example.mssaem_backend.domain.badge.BadgeRepository;
import com.example.mssaem_backend.domain.member.dto.MemberRequestDto.CheckNickName;
import com.example.mssaem_backend.domain.member.dto.MemberRequestDto.RegisterMember;
import com.example.mssaem_backend.domain.member.dto.MemberRequestDto.SocialLoginToken;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.CheckNickNameRes;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.TeacherInfo;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.TokenInfo;
import com.example.mssaem_backend.domain.worryboard.WorryBoardRepository;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.MemberErrorCode;
import com.example.mssaem_backend.global.config.security.jwt.JwtTokenProvider;
import com.example.mssaem_backend.global.config.security.oauth.SocialLoginService;
import com.example.mssaem_backend.global.config.security.oauth.SocialLoginType;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberService {

    private final SocialLoginService socialLoginService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final WorryBoardRepository worryBoardRepository;
    private final BadgeRepository badgeRepository;

    public void save(Member member) {
        memberRepository.save(member);
    }

    public Member register(RegisterMember registerMember) {
        Member member = Member.builder()
            .email(registerMember.getEmail())
            .nickName(registerMember.getNickname())
            .mbti(registerMember.getMbti())
            .caseSensitivity(registerMember.getCaseSensitivity())
            .refreshToken("")
            .report(0)
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
            case KAKAO -> email = socialLoginService.getKaKaoEmail(socialLoginService.getKaKaoAccessToken(idToken));
            case GOOGLE -> email = socialLoginService.getGoogleEmail(socialLoginService.getGoogleAccessToken(idToken));
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

    public CheckNickNameRes checkNickName(CheckNickName checkNickName) {
        return new CheckNickNameRes(
            memberRepository.existsByNickName(checkNickName.getNickName()));
    }

    // 홈 화면에 보여줄 인기 M쌤 조회
    public List<TeacherInfo> findHotTeacherForHome() {
        PageRequest pageRequest = PageRequest.of(0, 4);
        Page<Member> solveMembers = worryBoardRepository.findSolveMemberWithMoreThanOneIdAndIsSolvedTrueAndStateTrue(

            LocalDateTime.now().minusMonths(1),
            pageRequest);
        List<TeacherInfo> teacherInfos = new ArrayList<>();

        for (Member solveMember : solveMembers) {
            teacherInfos.add(
                new TeacherInfo(
                    solveMember.getId(),
                    solveMember.getNickName(),
                    solveMember.getDetailMbti(),
                    badgeRepository.findNameMemberAndStateTrue(solveMember).orElse(null),
                    solveMember.getProfileImageUrl(),
                    solveMember.getIntroduction()
                )
            );
        }
        return teacherInfos;
    }
}
