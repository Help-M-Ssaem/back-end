package com.example.mssaem_backend.domain.boardimage;

import com.example.mssaem_backend.domain.board.Board;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardImageRepository extends JpaRepository<BoardImage, Long> {

    BoardImage findTopByBoardOrderById(Board board);

    List<BoardImage> findAllByBoard(Long id);
}
