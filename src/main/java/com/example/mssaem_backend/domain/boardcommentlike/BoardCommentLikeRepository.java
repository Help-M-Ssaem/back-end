package com.example.mssaem_backend.domain.boardcommentlike;

import com.example.mssaem_backend.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCommentLikeRepository extends JpaRepository<BoardCommentLike, Long> {

    BoardCommentLike findByMemberAndStateIsTrue(Member member);

}
