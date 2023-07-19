package com.example.mssaem_backend.domain.worryboard;

import static com.example.mssaem_backend.global.common.Time.calculateTime;

import com.example.mssaem_backend.domain.badge.BadgeService;
import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardRequestDto.GetWorriesReq;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardResponseDto.GetWorriesRes;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardResponseDto.GetWorryRes;
import com.example.mssaem_backend.domain.worryboardimage.WorryBoardImageService;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.WorryBoardErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WorryBoardService {

    private final WorryBoardRepository worryBoardRepository;
    private final WorryBoardImageService worryBoardImageService;
    private final BadgeService badgeService;

    //List<WorryBoard>를 받아서 List<GetWorriesRes> 리스트를 반환하는 함수
    private List<GetWorriesRes> makeGetWorriesResForm(Page<WorryBoard> result) {
        return result
            .stream()
            .map(worryBoard -> GetWorriesRes.builder()
                .worryBoard(worryBoard)
                .imgUrl(worryBoardImageService.getImgUrl(worryBoard))
                .createdAt(calculateTime(worryBoard.getCreatedAt(), 3))
                .build())
            .toList();
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
                new MemberSimpleInfo(
                    member.getId(),
                    member.getNickName(),
                    member.getMbti(),
                    badgeService.findRepresentativeBadgeByMember(member),
                    member.getProfileImageUrl()
                )
            )
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

    // 홈 화면에 보여줄 해결안된 고민글 최신순으로 조회
    public List<GetWorriesRes> findWorriesForHome() {
        List<WorryBoard> worryBoards = worryBoardRepository.findTop7ByStateFalseOrderByCreatedAtDesc();
        if (!worryBoards.isEmpty()) {
            worryBoards.remove(0);
        }

        return worryBoards.stream()
            .map(worryBoard -> GetWorriesRes.builder()
                .worryBoard(worryBoard)
                .imgUrl(worryBoardImageService.getImgUrl(worryBoard))
                .createdAt(calculateTime(worryBoard.getCreatedAt(), 3))
                .build())
            .toList();
    }
}