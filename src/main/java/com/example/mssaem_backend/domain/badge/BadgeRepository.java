package com.example.mssaem_backend.domain.badge;

import com.example.mssaem_backend.domain.member.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    @Query("SELECT b.name FROM Badge b WHERE b.member = :member AND b.state = true")
    Optional<String> findNameMemberAndStateTrue(@Param("member") Member member);
}
