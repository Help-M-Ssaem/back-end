package com.example.mssaem_backend.domain.worryboard.dto;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class WorryBoardRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatchWorrySolvedReq {

        Long worrySolverId; //해결한 사람 id
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostWorryReq {

        String title;
        String content;
        MbtiEnum targetMbti;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatchWorryReq {

        String title;
        String content;
        MbtiEnum targetMbti;
    }
}