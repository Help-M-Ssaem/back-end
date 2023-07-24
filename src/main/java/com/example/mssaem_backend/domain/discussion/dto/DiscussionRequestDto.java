package com.example.mssaem_backend.domain.discussion.dto;

import com.example.mssaem_backend.domain.discussionoption.dto.DiscussionOptionRequestDto.GetOptionReq;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DiscussionRequestDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostDiscussionReq {
        String title;
        String content;
        List<GetOptionReq> getOptionReqs;
    }
}