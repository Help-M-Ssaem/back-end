package com.example.mssaem_backend.domain.like;

import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/member/boards/{boardId}/like")
    public ResponseEntity<Boolean> updateBoardLike(@CurrentMember Member member,
        @PathVariable(value = "boardId") Long boardId) {
        return ResponseEntity.ok(likeService.updateBoardLike(member, boardId));
    }
}
