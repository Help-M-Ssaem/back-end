package com.example.mssaem_backend.domain.boardcomment;

import com.example.mssaem_backend.domain.board.Board;
import com.example.mssaem_backend.domain.member.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {

    Long countByBoardAndStateTrue(Board board);

    List<BoardComment> findAllByBoardId(Long id);

    Long countAllByStateIsTrueAndMember(@Param("member") Member member);

    @Query(value = "select bc from BoardComment bc join fetch bc.member where bc.board.id = :id", countQuery = "select count(bc) from BoardComment bc")
    Page<BoardComment> findAllByBoardId(@Param("id") Long id, Pageable pageable);

    Optional<BoardComment> findByIdAndStateIsTrue(Long id);

    Boolean existsBoardCommentById(Long id);

    BoardComment findByIdAndBoardIdAndStateIsTrue(Long id, Long boardId);

    Page<BoardComment> findAllByMemberIdAndStateIsTrue(Long memberId, Pageable pageable);

    List<BoardComment> deleteAllByBoard(Board board);

}
