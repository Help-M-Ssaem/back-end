package com.example.mssaem_backend.global.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageResponseDto<T> {

    private int page;        // 몇 페이지인가
    private int totalSize;  // 총 페이지 개수가 몇 개인가
    private T result;         // 보여줄 List
}