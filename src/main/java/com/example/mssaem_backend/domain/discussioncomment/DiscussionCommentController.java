package com.example.mssaem_backend.domain.discussioncomment;

import com.example.mssaem_backend.domain.discussioncomment.dto.DiscussionCommentResponseDto.DiscussionCommentSimpleInfo;
import com.example.mssaem_backend.domain.discussioncomment.dto.DiscussionCommentResponseDto.DiscussionCommentSimpleInfo;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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
}
