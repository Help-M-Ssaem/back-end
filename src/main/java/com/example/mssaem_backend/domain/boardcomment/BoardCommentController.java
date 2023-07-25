package com.example.mssaem_backend.domain.boardcomment;

import com.example.mssaem_backend.domain.boardcomment.dto.BoardCommentResponseDto.BoardCommentSimpleInfo;
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
public class BoardCommentController {

    private final BoardCommentService boardCommentService;

    //특정 게시글 상세 조회시 댓글 전체 조회
    @GetMapping("/boards/comments/{boardId}")
    public ResponseEntity<PageResponseDto<List<BoardCommentSimpleInfo>>> findBoardCommentListByBoardId(
        @CurrentMember Member member, @PathVariable(value = "boardId") Long boardId,
        @RequestParam(value = "page") int page, @RequestParam(value = "size") int size) {
        return ResponseEntity.ok(
            boardCommentService.findBoardCommentListByBoardId(member, boardId, page, size));
    }

}
