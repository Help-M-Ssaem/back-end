package com.example.mssaem_backend.domain.boardcomment;

import com.example.mssaem_backend.domain.board.Board;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {

    Long countByBoardAndStateTrue(Board board);


    @Query(value = "select bc from BoardComment bc join fetch bc.member where bc.board.id = :id and bc.state = true ", countQuery = "select count(bc) from BoardComment bc")
    Page<BoardComment> findAllByBoardIdAndStateIsTrue(@Param("id") Long id, Pageable pageable);

    Optional<BoardComment> findByIdAndStateIsTrue(Long id);

}