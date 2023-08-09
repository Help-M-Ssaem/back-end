package com.example.mssaem_backend.domain.member;

import com.example.mssaem_backend.domain.badge.Badge;
import com.example.mssaem_backend.domain.badge.BadgeRepository;
import com.example.mssaem_backend.domain.badge.BadgeService;
import com.example.mssaem_backend.domain.board.Board;
import com.example.mssaem_backend.domain.board.BoardService;
import com.example.mssaem_backend.domain.discussion.DiscussionService;
import com.example.mssaem_backend.domain.evaluation.EvaluationService;
import com.example.mssaem_backend.domain.member.dto.MemberRequestDto.ModifyProfile;
import com.example.mssaem_backend.domain.member.dto.MemberRequestDto.CheckNickName;
import com.example.mssaem_backend.domain.member.dto.MemberRequestDto.RegisterMember;
import com.example.mssaem_backend.domain.member.dto.MemberRequestDto.SocialLoginToken;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberProfileInfo;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.CheckNickNameRes;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.TeacherInfo;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.TokenInfo;
import com.example.mssaem_backend.domain.worryboard.WorryBoardRepository;
import com.example.mssaem_backend.domain.worryboard.WorryBoardService;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.BadgeErrorCode;
import com.example.mssaem_backend.global.config.exception.errorCode.MemberErrorCode;
import com.example.mssaem_backend.global.config.security.jwt.JwtTokenProvider;
import com.example.mssaem_backend.global.config.security.oauth.SocialLoginService;
import com.example.mssaem_backend.global.config.security.oauth.SocialLoginType;
import com.example.mssaem_backend.global.s3.S3Service;
import com.example.mssaem_backend.global.s3.dto.S3Result;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.example.mssaem_backend.global.common.CheckWriter.isMatch;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberService {

    private final SocialLoginService socialLoginService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final WorryBoardRepository worryBoardRepository;
    private final BadgeRepository badgeRepository;
    private final S3Service s3Service;
    private final BadgeService badgeService;
    private final EvaluationService evaluationService;
    private final BoardService boardService;
    private final DiscussionService discussionService;
    private final WorryBoardService worryBoardService;

    public void save(Member member) {
        memberRepository.save(member);
    }

    public Member register(RegisterMember registerMember) {
        Member member = new Member(
                registerMember.getEmail(),
                registerMember.getNickname(),
                registerMember.getMbti(),
                registerMember.getCaseSensitivity());
        save(member);
        System.out.println("프로필 이미지는 " + member.getProfileImageUrl() + " 이고 디폴트 값은 " + member.isDefaultProfile());
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
            case NAVER -> email = socialLoginService.getNaverEmail(socialLoginService.getNaverAccessToken(idToken));
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

    public String modifyProfile(Member member, ModifyProfile modifyProfile, MultipartFile multipartFile) {
        // 프로필 사진 변경
        String profileImageUrl = uploadFile(member, multipartFile);
        // 대표 뱃지 변경
        String badgeName = badgeService.changeRepresentativeBadge(member, modifyProfile.getBadgeId());
        // 수정
        member.modifyMember(modifyProfile.getNickName(), modifyProfile.getIntroduction(),
                profileImageUrl, modifyProfile.getMbti(), modifyProfile.getCaseSensitivity(),
                badgeName);

        save(member);
        System.out.println("프로필 이미지는 " + member.getProfileImageUrl() + " 이고 디폴트 값은 " + member.isDefaultProfile());
        return "수정 성공";
    }

    public MemberProfileInfo getProfile(Long memberId) {
        Member member = memberRepository.findByIdWithStatus(memberId)
                .orElseThrow(()-> new BaseException(MemberErrorCode.EMPTY_MEMBER));
        return MemberProfileInfo.builder()
                .teacherInfo(new TeacherInfo(member))
                .badgeInfos(badgeService.findAllBadge(member))
                .evaluationCount(evaluationService.countEvaluation(member))
                .boardHistory(boardService.getBoardHistory(member))
                .discussionHistory(discussionService.getDiscussionHistory(member))
                .worryBoardHistory(worryBoardService.getWorryBoardHistory(member))
                .build();
    }

    public TokenInfo refreshAccessToken(Member member) {
        return TokenInfo.builder()
                .accessToken(jwtTokenProvider.generateAccessToken(member.getId()))
                .refreshToken(member.getRefreshToken())
                .build();
    }

    private String uploadFile(Member member, MultipartFile multipartFile) {
        if (multipartFile != null) {
            // 기본 이미지가 아니라면 기존 이미지 삭제 후 새로운 이미지 업로드, 기본 이미지가 삭제 되지 않기 위함
            if(!member.isDefaultProfile()) {
                s3Service.deleteFile(s3Service.parseFileName(member.getProfileImageUrl()));
            }
            member.changeFalseDefaultProfile();
            save(member);
            return s3Service.uploadImage(multipartFile);
        }
        return null;
    }
}
