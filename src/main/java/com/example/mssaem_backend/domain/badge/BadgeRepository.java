package com.example.mssaem_backend.domain.badge;

import com.example.mssaem_backend.domain.member.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    Optional<Badge> findBadgeWithStateTrueByMember(Member member);
}
