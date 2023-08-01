package com.example.mssaem_backend.domain.board.dto;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BoardRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostBoardReq {

        private String title;
        private String content;
        private MbtiEnum mbti;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatchBoardReq {

        private String title;
        private String content;
        private MbtiEnum mbti;
    }
}