package com.example.mssaem_backend.domain.chatparticipate;

import com.example.mssaem_backend.domain.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatParticipateRepository extends JpaRepository<ChatParticipate, Long> {

  @Query("select cp from ChatParticipate cp join fetch cp.chatRoom join fetch cp.member where cp.member = :member")
  List<ChatParticipate> findAllByMember(@Param("member") Member member);

  ChatParticipate findBySessionId(String sessionId);
}
