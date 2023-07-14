package com.example.mssaem_backend.global.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageResponseDto <T>{
    private int page;
    private int totalSize;
    private T result;
}
