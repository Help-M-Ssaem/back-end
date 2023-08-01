package com.example.mssaem_backend.domain.boardcommentlike;

import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BoardCommentLikeController {

    private final BoardCommentLikeService boardCommentLikeService;

    /**
     * 댓글 좋아요 누르기 API
     */
    @PostMapping("/member/boards/{boardId}/comments/{commentId}/like")
    public ResponseEntity<Boolean> updateBoardCommentLike(@CurrentMember Member member,
        @PathVariable(value = "boardId") Long boardId,
        @PathVariable(value = "commentId") Long commentId) {
        return ResponseEntity.ok(
            boardCommentLikeService.updateBoardCommentLike(member, boardId, commentId));
    }

}
