package com.example.mssaem_backend.domain.search;

import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.search.dto.SearchRequestDto.SearchInfo;
import com.example.mssaem_backend.domain.search.dto.SearchResponseDto.SearchPopular;
import com.example.mssaem_backend.domain.search.dto.SearchResponseDto.SearchRecent;
import com.example.mssaem_backend.domain.search.dto.SearchResponseDto.SearchRes;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SearchController {

    private final SearchService searchService;

    /**
     * 전체 검색어 입력
     */
    @PostMapping("/keywords")
    public ResponseEntity<SearchRes> selectKeywords(@CurrentMember Member member,
        @RequestBody SearchInfo searchInfo) {
        if (member != null) {
            searchService.insertKeywords(member, searchInfo);
        }
        searchService.insertRedisSearch(searchInfo);
        return ResponseEntity.ok(searchService.selectEverySearch(searchInfo, member));
    }

    /**
     * 저장된 검색어 10개 check
     */
    @GetMapping("/redis/searchtest")
    public List<SearchPopular> selectAllSearch() {
        return searchService.selectAllSearch();
    }

    /**
     * 최근 검색어
     */
    @GetMapping("/member/recent/keywords")
    public ResponseEntity<List<SearchRecent>> selectRecentSearch(@CurrentMember Member member) {
        return ResponseEntity.ok(searchService.selectRecentSearch(member));
    }

    /**
     * 실시간 인기 검색어
     */
    @GetMapping("/realtime/keywords")
    public ResponseEntity<List<SearchPopular>> selectAllPopularSearch() {
        return ResponseEntity.ok(searchService.selectPopularSearch());
    }

}
