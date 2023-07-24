package com.example.mssaem_backend.domain.bookmark.dto;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BookMarkResponseDto {

    @Getter
    @NoArgsConstructor
    public static class BookMarkInfo {

        private List<MbtiEnum> mbti;
        public BookMarkInfo (List<MbtiEnum> mbti){
            this.mbti = mbti;
        }
    }
}
