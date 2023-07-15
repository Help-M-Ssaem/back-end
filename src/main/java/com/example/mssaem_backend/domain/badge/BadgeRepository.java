package com.example.mssaem_backend.domain.badge;

import com.example.mssaem_backend.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    Badge findBadgeByMemberAndState(Member member, boolean state);
}
