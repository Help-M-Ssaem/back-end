package com.example.mssaem_backend.domain.evaluation.dto;

import com.example.mssaem_backend.domain.evaluation.EvaluationEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class EvaluationRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvaluationInfo{
        private Long worryBoardId;
        private List<EvaluationEnum> evaluations;
    }
}
