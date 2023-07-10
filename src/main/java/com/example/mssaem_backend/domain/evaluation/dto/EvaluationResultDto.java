package com.example.mssaem_backend.domain.evaluation.dto;

import com.example.mssaem_backend.domain.evaluation.EvaluationEnum;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class EvaluationResultDto {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class EvaluationResult{
    private Long worryBoardId;
    private List<EvaluationEnum> evaluations;
  }

}
