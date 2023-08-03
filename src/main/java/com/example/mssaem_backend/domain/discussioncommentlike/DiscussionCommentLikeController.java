package com.example.mssaem_backend.domain.discussioncommentlike;

import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class DiscussionCommentLikeController {

    private final DiscussionCommentLikeService discussionCommentLikeService;

    /**
     * 댓글 좋아요 누르기 API
     */
    @PostMapping("/member/discussions/{discussionId}/comments/{commentId}/like")
    public ResponseEntity<Boolean> updateDiscussionCommentLike(@CurrentMember Member member,
        @PathVariable Long discussionId, @PathVariable Long commentId) {
        return ResponseEntity.ok(discussionCommentLikeService.updateDiscussionCommentLike(member, discussionId, commentId));
    }
}
