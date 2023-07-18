package com.example.mssaem_backend.domain.like;

import com.example.mssaem_backend.domain.board.Board;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // 3일 동안 10개 이상 좋아요를 받은 게시물들을 3일 동안 받은 좋아요수 기준으로 내림차순 정렬
    @Query(value = "SELECT l.board FROM Like l WHERE l.createdAt >= :threeDaysAgo AND l.board.state = true GROUP BY l.board HAVING COUNT(l.board.id) >= 1 ORDER BY COUNT(l.board.id) DESC")
    Page<Board> findBoardsWithMoreThanTenLikesInLastThreeDaysAndStateTrue(@Param("threeDaysAgo") LocalDateTime threeDaysAgo, PageRequest pageRequest);

}