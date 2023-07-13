package com.example.mssaem_backend.domain.board;

import com.example.mssaem_backend.domain.board.dto.BoardResponseDto.BoardList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/boards/hot/{page}/{size}")
    public ResponseEntity<BoardList> findHotBoardList(@PathVariable int page, @PathVariable int size) {
        return ResponseEntity.ok(boardService.findHotBoardList(page, size));
    }
}
