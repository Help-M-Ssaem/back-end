package com.example.mssaem_backend.domain.boardcomment;

import com.example.mssaem_backend.domain.boardcomment.dto.BoardCommentRequestDto.PostBoardCommentReq;
import com.example.mssaem_backend.domain.boardcomment.dto.BoardCommentResponseDto.BoardCommentSimpleInfo;
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
public class BoardCommentController {

    private final BoardCommentService boardCommentService;

    //특정 게시글 상세 조회시 댓글 전체 조회
    @GetMapping("/boards/{boardId}/comments")
    public ResponseEntity<PageResponseDto<List<BoardCommentSimpleInfo>>> findBoardCommentListByBoardId(
        @CurrentMember Member member, @PathVariable(value = "boardId") Long boardId,
        @RequestParam(value = "page") int page, @RequestParam(value = "size") int size) {
        return ResponseEntity.ok(
            boardCommentService.findBoardCommentListByBoardId(member, boardId, page, size));
    }

    /**
     * 댓글 작성, 댓글 작성 시 @RequestParam 으로 commentId 값 받으면 대댓글 작성
     */
    @PostMapping("/member/boards/{boardId}/comments")
    public ResponseEntity<Boolean> createBoardComment(@CurrentMember Member member,
        @PathVariable(value = "boardId") Long boardId,
        @RequestPart(value = "postBoardCommentReq") PostBoardCommentReq postBoardCommentReq,
        @RequestParam(value = "commentId", required = false) Long commentId) {
        return ResponseEntity.ok(
            boardCommentService.createBoardComment(member, boardId, postBoardCommentReq,
                commentId));
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/member/boards/{boardId}/comments/{commentId}")
    public ResponseEntity<String> deleteBoardComment(@CurrentMember Member member,
        @PathVariable(value = "boardId") Long boardId,
        @PathVariable(value = "commentId") Long commentId) {
        return ResponseEntity.ok(
            boardCommentService.deleteBoardComment(member, boardId, commentId));
    }


}
