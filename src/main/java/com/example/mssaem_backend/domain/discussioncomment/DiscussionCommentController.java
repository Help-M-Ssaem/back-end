package com.example.mssaem_backend.domain.discussioncomment;

import com.example.mssaem_backend.domain.discussioncomment.dto.DiscussionCommentRequestDto.PostDiscussionCommentReq;
import com.example.mssaem_backend.domain.discussioncomment.dto.DiscussionCommentResponseDto.DiscussionCommentSimpleInfo;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class DiscussionCommentController {

    private final DiscussionCommentService discussionCommentService;

    //토론글 상세 조회시 댓글 조회
    @GetMapping("/discussions/{discussionId}/comments")
    public ResponseEntity<PageResponseDto<List<DiscussionCommentSimpleInfo>>> findDiscussionComments(
        @CurrentMember Member member, @PathVariable Long discussionId, @RequestParam int page,
        @RequestParam int size) {
        return ResponseEntity.ok(
            discussionCommentService.findDiscussionCommentListByDiscussionId(member, discussionId, page, size));
    }
    
    //토론글 상세 조회시 베스트 댓글 3개 조회
    @GetMapping("/discussions/{discussionId}/comments/best")
    public ResponseEntity<List<DiscussionCommentSimpleInfo>> findDiscussionCommentBestListByDiscussionId(
        @CurrentMember Member member, @PathVariable(value = "discussionId") Long discussionId) {
        return ResponseEntity.ok(
            discussionCommentService.findDiscussionCommentBestListByDiscussionId(member, discussionId));
    }

    /**
     * 댓글 작성, 댓글 작성 시 @RequestParam 으로 commentId 값 받으면 대댓글 작성
     */
    @PostMapping("/member/discussions/{discussionId}/comments")
    public ResponseEntity<Boolean> createDiscussionComment(@CurrentMember Member member,
        @PathVariable(value = "discussionId") Long discussionId,
        @RequestPart(value = "postDiscussionCommentReq") PostDiscussionCommentReq postDiscussionCommentReq,
        @RequestParam(value = "commentId", required = false) Long commentId) {
        return ResponseEntity.ok(
            discussionCommentService.createDiscussionComment(member, discussionId, postDiscussionCommentReq,
                commentId));
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/member/discussions/{discussionId}/comments/{commentId}")
    public ResponseEntity<Boolean> deleteDiscussionComment(@CurrentMember Member member,
        @PathVariable(value = "discussionId") Long discussionId,
        @PathVariable(value = "commentId") Long commentId) {
        return ResponseEntity.ok(
            discussionCommentService.deleteDiscussionComment(member, discussionId, commentId));
    }
}
