package com.example.mssaem_backend.domain.worryboard.dto;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class WorryBoardRequestDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetWorriesReq {
        String fromMbti;
        String toMbti;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatchWorrySolvedReq {
        Long worryBoardId;
        Long worrySolverId; //해결한 사람 id
    }

}

