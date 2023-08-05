package com.example.mssaem_backend.domain.discussionoption.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DiscussionOptionResponseDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiscussionOptionInfo {

        private Long id;
        private String content;
        private String imgUrl;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiscussionOptionLoginInfo {

        private Long id;
        private String content;
        private String imgUrl;
        private String selectedPercent;
        private boolean selected;
    }
}
