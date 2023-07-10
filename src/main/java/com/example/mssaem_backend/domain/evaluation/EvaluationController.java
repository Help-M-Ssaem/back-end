package com.example.mssaem_backend.domain.evaluation;

import com.example.mssaem_backend.domain.evaluation.dto.EvaluationRequestDto.EvaluationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class EvaluationController {
  private final EvaluationService evaluationService;

  /**
   * 평가 추가
   */
  @PostMapping("/evaluations")
  public ResponseEntity<String> insertEvaluation(@RequestBody EvaluationInfo evaluationInfo) {
    return ResponseEntity.ok(evaluationService.insertEvaluation(evaluationInfo));
  }

}
