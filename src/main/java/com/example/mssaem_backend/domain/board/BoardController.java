package com.example.mssaem_backend.domain.board;

import com.example.mssaem_backend.domain.board.dto.BoardRequestDto.DeleteBoardReq;
import com.example.mssaem_backend.domain.board.dto.BoardRequestDto.PatchBoardReq;
import com.example.mssaem_backend.domain.board.dto.BoardRequestDto.PostBoardReq;
import com.example.mssaem_backend.domain.board.dto.BoardResponseDto.BoardSimpleInfo;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/boards/hot")
    public ResponseEntity<PageResponseDto<List<BoardSimpleInfo>>> findHotBoardList(
        @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(boardService.findHotBoardList(page, size));
    }

    @GetMapping("/boards/home")
    public ResponseEntity<List<BoardSimpleInfo>> findHotBoardListForHome() {
        return ResponseEntity.ok(boardService.findHotBoardListForHome());
    }

    /**
     * 게시글 생성
     */
    @PostMapping("/member/board")
    public ResponseEntity<String> createBoard(@CurrentMember Member member,
        @RequestPart(value = "postBoardReq") PostBoardReq postBoardReq,
        @RequestPart(value = "image", required = false) List<MultipartFile> multipartFiles) {
        return ResponseEntity.ok(boardService.createBoard(member, postBoardReq, multipartFiles));
    }

    /**
     * 게시글 수정
     */
    @PatchMapping("/member/board/{id}")
    public ResponseEntity<String> modifyBoard(@CurrentMember Member member,
        @RequestPart(value = "patchBoardReq") PatchBoardReq patchBoardReq, @PathVariable Long id,
        @RequestPart(value = "image", required = false) List<MultipartFile> multipartFiles) {
        return ResponseEntity.ok(
            boardService.modifyBoard(member, patchBoardReq, id, multipartFiles));
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/member/board/{id}")
    public ResponseEntity<String> deleteBoard(@CurrentMember Member member,
        @RequestBody DeleteBoardReq deleteBoardReq, @PathVariable Long id) {
        return ResponseEntity.ok(boardService.deleteBoard(member, deleteBoardReq, id));
    }
}
