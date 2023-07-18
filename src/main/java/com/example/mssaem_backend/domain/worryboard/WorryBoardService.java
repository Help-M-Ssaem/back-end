package com.example.mssaem_backend.domain.worryboard;

import static com.example.mssaem_backend.global.common.Time.calculateTime;

import com.example.mssaem_backend.domain.badge.BadgeService;
import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.MemberRepository;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardRequestDto.GetWorriesReq;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardRequestDto.PatchWorryReq;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardRequestDto.PatchWorrySolvedReq;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardRequestDto.PostWorryReq;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardResponseDto.GetWorriesRes;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardResponseDto.GetWorryRes;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardResponseDto.PatchWorrySolvedRes;
import com.example.mssaem_backend.domain.worryboardimage.WorryBoardImageService;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.MemberErrorCode;
import com.example.mssaem_backend.global.config.exception.errorCode.WorryBoardErrorCode;
import com.example.mssaem_backend.global.s3.S3Service;
import com.example.mssaem_backend.global.s3.dto.S3Result;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class WorryBoardService {

    private final WorryBoardRepository worryBoardRepository;
    private final WorryBoardImageService worryBoardImageService;
    private final MemberRepository memberRepository;
    private final BadgeService badgeService;
    private final S3Service s3Service;

    //List<WorryBoard>를 받아서 List<GetWorriesRes> 리스트를 반환하는 함수
    private List<GetWorriesRes> makeGetWorriesResForm(Page<WorryBoard> result) {
        return result.stream().map(worryBoard -> GetWorriesRes.builder().worryBoard(worryBoard)
            .imgUrl(worryBoardImageService.getImgUrl(worryBoard))
            .createdAt(calculateTime(worryBoard.getCreatedAt(), 3)).build()).toList();
    }

    //고민게시판 - 고민 목록 조회
    public PageResponseDto<List<GetWorriesRes>> findWorriesByState(boolean state, int page,
        int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<WorryBoard> result = worryBoardRepository.findByState(state, pageable);
        return new PageResponseDto<>(result.getNumber(), result.getTotalPages(),
            makeGetWorriesResForm(result));
    }

    //고민 게시판 - 고민글 상세 조회
    public GetWorryRes findWorryById(Member viewer, Long id) {
        WorryBoard worryBoard = worryBoardRepository.findById(id)
            .orElseThrow(() -> new BaseException(WorryBoardErrorCode.EMPTY_WORRY_BOARD));
        Member member = worryBoard.getMember();

        //수정,삭제 권한 확인
        Boolean isAllowed = (viewer != null && viewer.equals(member));

        return GetWorryRes.builder()
            .worryBoard(worryBoard)
            .imgList(worryBoardImageService.getImgUrls(worryBoard))
            .createdAt(calculateTime(worryBoard.getCreatedAt(), 2))
            .memberSimpleInfo(
                new MemberSimpleInfo(member.getId(), member.getNickName(), member.getMbti(),
                    badgeService.findRepresentativeBadgeByMember(member),
                    member.getProfileImageUrl()))
            .isAllowed(isAllowed)
            .build();
    }

    //특정 멤버별 올린 고민 목록 조회
    public PageResponseDto<List<GetWorriesRes>> findWorriesByMemberId(Long memberId, int page,
        int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<WorryBoard> result = worryBoardRepository.findByMemberId(memberId, pageable);
        return new PageResponseDto<>(result.getNumber(), result.getTotalPages(),
            makeGetWorriesResForm(result));
    }

    //특정 멤버별 해결한 고민 목록 조회
    public PageResponseDto<List<GetWorriesRes>> findSolveWorriesByMemberId(Long memberId, int page,
        int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<WorryBoard> result = worryBoardRepository.findBySolveMemberId(memberId, pageable);
        return new PageResponseDto<>(result.getNumber(), result.getTotalPages(),
            makeGetWorriesResForm(result));
    }

    // mbti 필터링 조회
    public PageResponseDto<List<GetWorriesRes>> findWorriesByMbti(GetWorriesReq getWorriesReq,
        Boolean isSolved, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        boolean isFromAll = getWorriesReq.getFromMbti().equals("ALL");
        boolean isToAll = getWorriesReq.getToMbti().equals("ALL");

        // ALL인 경우에 mbti에는 null값이 들어감
        MbtiEnum fromMbti = isFromAll ? null : MbtiEnum.valueOf(getWorriesReq.getFromMbti());
        MbtiEnum toMbti = isToAll ? null : MbtiEnum.valueOf(getWorriesReq.getToMbti());

        Page<WorryBoard> result = worryBoardRepository.findWorriesByStateAndBothMbti(isSolved,
            fromMbti, toMbti, pageable);

        return new PageResponseDto<>(result.getNumber(), result.getTotalPages(),
            makeGetWorriesResForm(result));
    }

    // 고민 해결 완료
    public PatchWorrySolvedRes solvedWorryBoard(PatchWorrySolvedReq patchWorryReq) {
        WorryBoard worryBoard = worryBoardRepository.findById(patchWorryReq.getWorryBoardId())
            .orElseThrow(() -> new BaseException(WorryBoardErrorCode.EMPTY_WORRY_BOARD));

        Member solveMember = memberRepository.findById(patchWorryReq.getWorrySolverId())
            .orElseThrow(() -> new BaseException(MemberErrorCode.EMPTY_MEMBER));
        // 고민글의 상태 바꾸기
        worryBoard.solveWorryBoard(false, solveMember);
        // 평가창에 뜰 memberSimpleInfo, worryBoard Id 반환
        return PatchWorrySolvedRes.builder()
            .memberSimpleInfo(
                new MemberSimpleInfo(
                solveMember.getId(), solveMember.getNickName(),
                solveMember.getMbti(), badgeService.findRepresentativeBadgeByMember(solveMember),
                solveMember.getProfileImageUrl())
            )
            .worryBoardId(worryBoard.getId()).build();
    }

    //고민글 생성
    public String createWorryBoard(Member currentMember, PostWorryReq postWorryReq,
        List<MultipartFile> multipartFiles) {
        //고민글 내용 저장
        WorryBoard worryBoard = WorryBoard.builder()
            .title(postWorryReq.getTitle())
            .content(postWorryReq.getContent())
            .targetMbti(postWorryReq.getTargetMbti())
            .member(currentMember)
            .build();
        worryBoardRepository.save(worryBoard);

        //worryBoardImageService에 전달
        if (multipartFiles != null) {
            worryBoardImageService.uploadWorryImage(worryBoard, multipartFiles);
        }
        return "고민글 생성 완료";
    }

    //고민글 수정
    public String modifyWorryBoard(Member currentMember, PatchWorryReq patchWorryReq,
        List<MultipartFile> multipartFiles) {
        //현재 멤버와 작성자 일치하는 지 확인
        WorryBoard worryBoard = worryBoardRepository.findById(patchWorryReq.getWorryBoardId())
            .orElseThrow(() -> new BaseException(WorryBoardErrorCode.EMPTY_WORRY_BOARD));
        if (!currentMember.getId().equals(worryBoard.getMember().getId())) {
            throw new BaseException(MemberErrorCode.INVALID_MEMBER);
        }

        //worryBoard 수정하기
        worryBoard.modifyWorryBoard(patchWorryReq.getTitle(), patchWorryReq.getContent(),
            patchWorryReq.getTargetMbti());

        //worryBoardImageService에 있던 imgUrl 삭제
        worryBoardImageService.deleteWorryImage(worryBoard);

        //S3에서 파일 삭제하기 && 해당 worryBoard의 worryBoardImage 삭제
        List<String> worryBoardImgUrls = worryBoardImageService.getImgUrls(worryBoard);
        worryBoardImgUrls.stream().map(s3Service::parseFileName).forEach(s3Service::deleteFile);

        //S3 새로운 파일 업로드 하기 && worryBoard의 worryBoardImage 생성
        if (multipartFiles != null) {
            List<S3Result> worryBoardImageList = s3Service.uploadFile(multipartFiles);
            if (!worryBoardImageList.isEmpty()) {
                for (S3Result s3Result : worryBoardImageList) {
                    worryBoardImageService.uploadWorryImage(worryBoard, multipartFiles);
                }
            }
        }

        return "고민글 수정 완료";
    }
}