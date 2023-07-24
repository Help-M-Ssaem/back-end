package com.example.mssaem_backend.domain.board;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    //boardId가 입력되지 않으면 전체 게시글 조회, 입력되면 해당 게시글만 제외하고 전체 조회
    @Query("SELECT b FROM Board b WHERE b.state = true AND (:boardId IS NULL OR b.id <> :boardId)")
    Page<Board> findAllByStateIsTrueAndId(@Param("boardId") Long boardId, Pageable pageable);

    Page<Board> findAllByStateIsTrueAndMbti(MbtiEnum mbtiEnum, Pageable pageable);

    Page<Board> findAllByMemberIdAndStateIsTrue(Long memberId, Pageable pageable);

    // 전체 게시판 검색하기
    @Query("SELECT b FROM Board b WHERE (LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')) OR LOWER(b.content) LIKE LOWER(CONCAT('%', :content, '%'))) AND b.state = true ORDER BY b.createdAt DESC")
    Page<Board> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndStateTrueOrderByCreatedAtDesc(
        @Param("title") String title, @Param("content") String content, PageRequest pageRequest);

    Page<Board> findByTitleContainingIgnoreCaseAndStateTrueOrderByCreatedAtDesc(String title,
        PageRequest pageRequest);

    Page<Board> findByContentContainingIgnoreCaseAndStateTrueOrderByCreatedAtDesc(String content,
        PageRequest pageRequest);

    Page<Board> findByMemberNickNameContainingIgnoreCaseAndStateTrueOrderByCreatedAtDesc(
        String nickName, PageRequest pageRequest);

    // Mbti 카테고리 별 검색하기
    @Query("SELECT b FROM Board b WHERE (LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')) OR LOWER(b.content) LIKE LOWER(CONCAT('%', :content, '%'))) AND b.state = true AND b.mbti = :mbti ORDER BY b.createdAt DESC")
    Page<Board> findByMbtiAndStateTrueAndTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByCreatedAtDesc(
        @Param("title") String title, @Param("content") String content,
        @Param("mbti") MbtiEnum mbti, PageRequest pageRequest);

    Page<Board> findByTitleContainingIgnoreCaseAndMbtiAndStateTrueOrderByCreatedAtDesc(String title,
        MbtiEnum mbti,
        PageRequest pageRequest);

    Page<Board> findByContentContainingIgnoreCaseAndMbtiAndStateTrueOrderByCreatedAtDesc(
        String content, MbtiEnum mbti,
        PageRequest pageRequest);

    Page<Board> findByMemberNickNameContainingIgnoreCaseAndMbtiAndStateTrueOrderByCreatedAtDesc(
        String nickName, MbtiEnum mbti, PageRequest pageRequest);
}
