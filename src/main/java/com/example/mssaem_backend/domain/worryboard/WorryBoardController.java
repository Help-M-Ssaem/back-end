package com.example.mssaem_backend.domain.worryboard;

import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardResponseDto.GetWorriesRes;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardResponseDto.GetWorryRes;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class WorryBoardController {

  private final WorryBoardService worryBoardService;

  //고민글 조회 (해결 X)
  @GetMapping("/worry-board/waiting")
  public ResponseEntity<List<GetWorriesRes>> findWorriesWaiting() {
    return ResponseEntity.ok(worryBoardService.findWorriesByState(false));
  }

  //고민글 조회 (해결 O)
  @GetMapping("/worry-board/solved")
  public ResponseEntity<List<GetWorriesRes>> findWorriesSolved() {
    return ResponseEntity.ok(worryBoardService.findWorriesByState(true));
  }

  //고민글 상세 조회
  @GetMapping("/worry-board/{id}")
  public ResponseEntity<GetWorryRes> findWorry(@PathVariable Long id) {
    return ResponseEntity.ok(worryBoardService.findWorryById(id));
  }
}
