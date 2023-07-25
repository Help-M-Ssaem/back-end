package com.example.mssaem_backend.domain.search;

import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.search.dto.SearchRequestDto.SearchInfo;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SearchController {
  private final SearchService searchService;

  /**
   * 전체 검색어 저장,
   */
  @PostMapping("/keywords")
  public ResponseEntity<String> selectKeywords(@CurrentMember Member member, @RequestBody SearchInfo searchInfo){
    if(member != null) searchService.insertKeywords(member, searchInfo);
    return ResponseEntity.ok("");
  }
}
