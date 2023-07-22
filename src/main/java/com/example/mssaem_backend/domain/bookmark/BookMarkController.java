package com.example.mssaem_backend.domain.bookmark;

import com.example.mssaem_backend.domain.bookmark.dto.BookMarkResponseDto.BookMarkInfo;
import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BookMarkController {

    private final BookMarkService bookMarkService;

    //카테고리 즐겨찾기 누르기
    @PostMapping("/member/bookmark")
    public ResponseEntity<String> updateBookMark(@CurrentMember Member member,
        @RequestParam MbtiEnum mbtiEnum) {
        return ResponseEntity.ok(bookMarkService.updateBookMark(member, mbtiEnum));
    }
    //즐겨찾기 목록 조회
    @GetMapping("/member/bookmark")
    public ResponseEntity<List<BookMarkInfo>> findBookMarkList(@CurrentMember Member member) {
        return ResponseEntity.ok(bookMarkService.getBookMarkList(member));
    }

}
