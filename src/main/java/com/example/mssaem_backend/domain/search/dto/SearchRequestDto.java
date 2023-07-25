package com.example.mssaem_backend.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SearchRequestDto {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SearchInfo{
    private String keyword;
  }

}
