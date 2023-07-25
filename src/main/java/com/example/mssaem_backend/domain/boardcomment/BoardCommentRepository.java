package com.example.mssaem_backend.domain.boardcomment;

import com.example.mssaem_backend.domain.board.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {

    Long countByBoardAndStateTrue(Board board);

    Page<BoardComment> findAllByBoardIdAndStateIsTrue(Long id , Pageable pageable);
}
