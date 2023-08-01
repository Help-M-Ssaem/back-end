package com.example.mssaem_backend.domain.boardcommentlike;

import com.example.mssaem_backend.domain.boardcomment.BoardComment;
import com.example.mssaem_backend.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardCommentLikeRepository extends JpaRepository<BoardCommentLike, Long> {

    Boolean existsBoardCommentLikeByMemberAndStateIsTrueAndBoardCommentId(Member member,
        Long boardCommentId);

    // 10개 이상 좋아요를 받은 댓글들을 좋아요수 기준으로 내림차순 정렬
    @Query(value = "SELECT bc FROM BoardComment bc WHERE bc.board.id = :boardId AND bc.likeCount >= 10 AND bc.state = true ORDER BY bc.likeCount DESC")
    Page<BoardComment> findBoardCommentsByBoardIdWithMoreThanTenBoardCommentLikeAndStateTrue(
        PageRequest pageRequest, @Param("boardId") Long boardId);


    BoardCommentLike findBoardCommentLikeByMemberAndBoardCommentId(Member member,
        Long boardCommentId);

    Boolean existsBoardCommentLikeByMemberAndBoardCommentId(Member member, Long bardCommentId);
}
