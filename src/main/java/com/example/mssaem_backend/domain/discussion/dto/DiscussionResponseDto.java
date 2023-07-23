package com.example.mssaem_backend.domain.discussion.dto;

import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DiscussionResponseDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiscussionSimpleInfo<T> {

        private Long id;
        private String title;
        private String content;
        private Long participantCount;
        private Long commentCount;
        private String createdAt;
        private MemberSimpleInfo memberSimpleInfo;
        private List<T> options;
    }
}