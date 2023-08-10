package com.example.mssaem_backend.domain.like;

import com.example.mssaem_backend.domain.board.Board;
import com.example.mssaem_backend.domain.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Boolean existsLikeByMemberAndStateIsTrueAndBoard(Member member, Board board);

    Like findByMemberAndBoardId(Member member, Long id);

    Boolean existsLikeByMemberAndBoardId(Member member, Long id);

    @Query(value = "SELECT COUNT(l) FROM Like l WHERE l.board.member = :member AND l.board.state = true AND l.state = true")
    Integer countLikesByMember(@Param("member") Member member);

    List<Like> deleteAllByBoard(Board board);
}