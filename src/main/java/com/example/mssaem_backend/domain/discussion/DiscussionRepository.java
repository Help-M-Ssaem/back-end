package com.example.mssaem_backend.domain.discussion;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {

    // 생성된지 3일 이내이고, 참여자수가 10명 이상인 토론글들을 참여자수를 기준으로 내림차순 정렬
    @Query(value = "SELECT d FROM Discussion d WHERE d.createdAt >= :threeDaysAgo AND d.participants >= 1 ORDER BY d.participants DESC")
    Page<Discussion> findDiscussionWithMoreThanTenParticipantsInLastThreeDays(
        @Param("threeDaysAgo") LocalDateTime threeDaysAgo, PageRequest pageRequest);
}
