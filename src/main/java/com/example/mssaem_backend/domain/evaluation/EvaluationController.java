package com.example.mssaem_backend.domain.evaluation;

import com.example.mssaem_backend.domain.evaluation.dto.EvaluationRequestDto.EvaluationInfo;
import com.example.mssaem_backend.domain.evaluation.dto.EvaluationResultDto.EvaluationCount;
import com.example.mssaem_backend.domain.evaluation.dto.EvaluationResultDto.EvaluationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/evaluations")
public class EvaluationController {

  private final EvaluationService evaluationService;

  /**
   * 평가 추가
   */
  @PostMapping("")
  public ResponseEntity<String> insertEvaluation(@RequestBody EvaluationInfo evaluationInfo) {
    return ResponseEntity.ok(evaluationService.insertEvaluation(evaluationInfo));
  }

  /**
   * 평가 받은 사람의 평가 내용 조회
   */
  @GetMapping("/{worryBoardId}")
  public ResponseEntity<EvaluationResult> selectEvaluation(@PathVariable Long worryBoardId){
    return ResponseEntity.ok(evaluationService.selectEvaluation(worryBoardId));
  }

  /**
   * 자신이 받은 평가 count
   */
  @GetMapping("/count/{memberId}")
  public ResponseEntity<EvaluationCount> countEvaluation(@PathVariable Long memberId){
    return ResponseEntity.ok(evaluationService.countEvaluation(memberId));
  }

}
