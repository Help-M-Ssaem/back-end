package com.example.mssaem_backend.domain.board;

import com.example.mssaem_backend.domain.board.dto.BoardResponseDto.BoardSimpleInfo;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
