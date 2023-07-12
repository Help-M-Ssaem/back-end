package com.example.mssaem_backend.domain.worryboard;

import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardResponseDto.GetWorriesRes;
import com.example.mssaem_backend.domain.worryboardimage.WorryBoardImageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WorryBoardService {

  private final WorryBoardRepository worryBoardRepository;
  private final WorryBoardImageService worryBoardImageService;

  //고민글 조회 (
  public List<GetWorriesRes> findWorriesByState(boolean state) {
    List<WorryBoard> worryBoards = worryBoardRepository.findByState(state);

    List<GetWorriesRes>  getWorriesResList = worryBoards
        .stream()
        .map(worryBoard -> new GetWorriesRes(worryBoard, worryBoardImageService.getImgUrl(worryBoard) ))
        .toList();

    return getWorriesResList;
  }
}
