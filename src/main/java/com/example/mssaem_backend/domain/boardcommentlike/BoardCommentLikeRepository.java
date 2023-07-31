package com.example.mssaem_backend.domain.boardcommentlike;

import com.example.mssaem_backend.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCommentLikeRepository extends JpaRepository<BoardCommentLike, Long> {

    Boolean existsBoardCommentLikeByMemberAndStateIsTrueAndBoardCommentId(Member member,
        Long boardCommentId);

    BoardCommentLike findBoardCommentLikeByMemberAndBoardCommentId(Member member,
        Long boardCommentId);

    Boolean existsBoardCommentLikeByMemberAndBoardCommentId(Member member, Long bardCommentId);
}
