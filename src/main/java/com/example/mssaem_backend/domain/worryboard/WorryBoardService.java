package com.example.mssaem_backend.domain.worryboard;

import static com.example.mssaem_backend.global.common.Time.calculateTime;

import com.example.mssaem_backend.domain.badge.BadgeService;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardResponseDto.GetWorriesRes;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardResponseDto.GetWorryRes;
import com.example.mssaem_backend.domain.worryboardimage.WorryBoardImageService;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.WorryBoardErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WorryBoardService {

    private final WorryBoardRepository worryBoardRepository;
    private final WorryBoardImageService worryBoardImageService;
    private final BadgeService badgeService;

    //고민게시판 - 고민 목록 조회 (
    public List<GetWorriesRes> findWorriesByState(boolean state) {
        List<WorryBoard> worryBoards = worryBoardRepository.findByState(state);
        return worryBoards
            .stream()
            .map(worryBoard -> GetWorriesRes.builder()
                .worryBoard(worryBoard)
                .imgUrl(worryBoardImageService.getImgUrl(worryBoard))
                .createdAt(calculateTime(worryBoard.getCreatedAt(), 3))
                .build())
            .toList();
    }

    //고민 게시판 - 고민글 상세 조회
    public GetWorryRes findWorryById(Long id) {
        WorryBoard worryBoard = worryBoardRepository.findById(id)
            .orElseThrow(() -> new BaseException(WorryBoardErrorCode.EMPTY_WORRY_BOARD));

        return GetWorryRes.builder()
            .worryBoard(worryBoard)
            .imgList(worryBoardImageService.getImgUrls(worryBoard))
            .representativeBadge(
                badgeService.findRepresentativeBadgeByMember(worryBoard.getMember()))
            .createdAt(calculateTime(worryBoard.getCreatedAt(), 2))
            .build();
    }

    //특정 멤버별 올린 고민 목록 조회
    public List<GetWorriesRes> findWorriesByMemberId(Long memberId) {
        List<WorryBoard> worryBoards = worryBoardRepository.findByMemberId(memberId);
        return worryBoards
            .stream()
            .map(worryBoard -> GetWorriesRes.builder()
                .worryBoard(worryBoard)
                .imgUrl(worryBoardImageService.getImgUrl(worryBoard))
                .createdAt(calculateTime(worryBoard.getCreatedAt(), 3))
                .build())
            .toList();
    }

    //특정 멤버별 해결한 고민 목록 조회
    public List<GetWorriesRes> findSolveWorriesByMemberId(Long memberId) {
        List<WorryBoard> worryBoards = worryBoardRepository.findBySolveMemberId(memberId);
        return worryBoards
            .stream()
            .map(worryBoard -> GetWorriesRes.builder()
                .worryBoard(worryBoard)
                .imgUrl(worryBoardImageService.getImgUrl(worryBoard))
                .createdAt(calculateTime(worryBoard.getCreatedAt(), 3))
                .build())
            .toList();
    }
}
