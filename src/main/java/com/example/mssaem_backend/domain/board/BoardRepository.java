package com.example.mssaem_backend.domain.board;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    //boardId가 입력되지 않으면 전체 게시글 조회, 입력되면 해당 게시글만 제외하고 잔체 조회
    @Query("SELECT b FROM Board b WHERE b.state = true AND (:boardId IS NULL OR b.id <> :boardId)")
    Page<Board> findAllByStateIsTrueAndId(@Param("boardId") Long boardId, Pageable pageable);

    Page<Board> findAllByStateIsTrueAndMbti(MbtiEnum mbtiEnum, Pageable pageable);

    Page<Board> findAllByMemberIdAndStateIsTrue(Long memberId, Pageable pageable);
}
