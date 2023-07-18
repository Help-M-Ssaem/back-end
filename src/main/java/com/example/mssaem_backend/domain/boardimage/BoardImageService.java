package com.example.mssaem_backend.domain.boardimage;

import com.example.mssaem_backend.domain.board.Board;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BoardImageService {

    private final BoardImageRepository boardImageRepository;

    public String uploadImage(String boardImageUrl, Board board) {
        BoardImage boardImage = new BoardImage(boardImageUrl);
        boardImage.setBoard(board);
        boardImageRepository.save(boardImage);
        return "이미지 업로드 완료";
    }

    public List<BoardImage> loadImage(Long boardId) {
        return boardImageRepository.findAllByBoardId(boardId);
    }

    public void deleteImage(Board board) {
        boardImageRepository.deleteAllByBoard(board);
    }
}
