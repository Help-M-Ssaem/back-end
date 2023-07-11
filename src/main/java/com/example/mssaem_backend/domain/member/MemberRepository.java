package com.example.mssaem_backend.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query(value = "select m from Member m where m.id = :memberId and m.status = false")
    Optional<Member> findByIdWithStatus(Long memberId);
    Optional<Member> findByEmail(String email);

    Boolean existsByNickName(String nickName);
    Boolean existsByEmail(String email);
}
