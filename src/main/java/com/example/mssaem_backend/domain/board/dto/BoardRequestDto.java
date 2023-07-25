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
        private Long memberId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatchBoardReq {

        private String title;
        private String content;
        private MbtiEnum mbti;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchBoardReq {

        private int type; // 제목+내용, 제목, 내용, 글쓴이로 순서대로 0,1,2,3을 의미
        private String keyword; // 검색어
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchBoardByMbtiReq {

        private int type; // 제목+내용, 제목, 내용, 글쓴이로 순서대로 0,1,2,3을 의미
        private String keyword; // 검색어
        private MbtiEnum mbti;
    }
}