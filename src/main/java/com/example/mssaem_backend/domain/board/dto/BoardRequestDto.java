package com.example.mssaem_backend.domain.board.dto;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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
        private Long memberId;
        private List<MultipartFile> files;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteBoardReq {

        private Long memberId;
    }

}