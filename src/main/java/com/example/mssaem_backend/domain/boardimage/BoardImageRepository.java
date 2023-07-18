package com.example.mssaem_backend.domain.boardimage;

import com.example.mssaem_backend.domain.board.Board;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardImageRepository extends JpaRepository<BoardImage, Long> {

    Optional<BoardImage> findTopByBoardOrderById(Board board);

    List<BoardImage> findAllByBoardId(Long id);

    List<BoardImage> deleteAllByBoard(Board board);
}
