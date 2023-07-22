package com.example.mssaem_backend.domain.bookmark.dto;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BookMarkResponseDto {

    @Getter
    @NoArgsConstructor
    public static class BookMarkInfo {

        private Long memberId;
        private MbtiEnum mbti;
        @Builder
        public BookMarkInfo (Long memberId , MbtiEnum mbti){
            this.memberId = memberId;
            this.mbti = mbti;
        }
    }
}
