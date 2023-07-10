package com.example.mssaem_backend.domain.evaluation.dto;

import com.example.mssaem_backend.domain.evaluation.EvaluationEnum;
import com.example.mssaem_backend.domain.member.Member;
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

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class EvaluationCount{
    private Member member;
    private int likeCount;
    private int usefulCount;
    private int funCount;
    private int sincereCount;
    private int hotCount;

    public EvaluationCount(Member member, char[] result) {
      this.member = member;
      this.likeCount = result[0] - '0';
      this.usefulCount = result[1] - '0';
      this.funCount = result[2] - '0';
      this.sincereCount = result[3] - '0';
      this.hotCount = result[4] - '0';
    }
  }

}
