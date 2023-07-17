package com.example.mssaem_backend.domain.board.dto;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BoardResponseDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BoardSimpleInfo {

        private Long id;
        private String title;
        private String content;
        private String imgUrl;
        private MbtiEnum boardMbti;
        private Long likeCount;
        private Long commentCount;
        private String createdAt;
        private MemberSimpleInfo memberSimpleInfo;
    }
}