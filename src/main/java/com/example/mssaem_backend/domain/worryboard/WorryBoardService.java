package com.example.mssaem_backend.domain.worryboard;

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

  //고민게시판 - 고민글 목록 조회 (
  public List<GetWorriesRes> findWorriesByState(boolean state) {
    List<WorryBoard> worryBoards = worryBoardRepository.findByState(state);
    return worryBoards
        .stream()
        .map(worryBoard -> new GetWorriesRes(worryBoard,
            worryBoardImageService.getImgUrl(worryBoard)))
        .toList();
  }

  //고민 게시판 - 고민글 상세 조회
  public GetWorryRes findWorryById(Long id) {
    WorryBoard worryBoard = worryBoardRepository.findById(id)
        .orElseThrow(() -> new BaseException(WorryBoardErrorCode.EMPTY_WORRYBOARD));
    return new GetWorryRes(worryBoard, worryBoardImageService.getImgUrls(worryBoard));
  }

  //특정 멤버 별 올린 고민 조회
  public List<GetWorriesRes> findWorriesByMemberId(Long memberId) {
    List<WorryBoard> worryBoards = worryBoardRepository.findByMemberId(memberId);
    return worryBoards
        .stream()
        .map(worryBoard -> new GetWorriesRes(worryBoard,
            worryBoardImageService.getImgUrl(worryBoard)))
        .toList();
  }
}
