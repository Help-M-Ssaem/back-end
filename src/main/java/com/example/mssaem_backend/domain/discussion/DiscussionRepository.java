package com.example.mssaem_backend.domain.discussion;

import java.time.LocalDateTime;

import com.example.mssaem_backend.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {

    // 생성된지 3일 이내이고, 참여자수가 10명 이상인 토론글들을 참여자수를 기준으로 내림차순 정렬
    @Query(value = "SELECT d FROM Discussion d WHERE d.createdAt >= :threeDaysAgo AND d.participantCount >= 1 AND d.state = true ORDER BY d.participantCount DESC")
    Page<Discussion> findDiscussionWithMoreThanTenParticipantsInLastThreeDaysAndStateTrue(
        @Param("threeDaysAgo") LocalDateTime threeDaysAgo, PageRequest pageRequest);


    @Query("SELECT d FROM Discussion d WHERE"
        + "(    (:type = 0 AND (LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(d.content) LIKE LOWER(CONCAT('%', :keyword, '%'))))"
        + " OR (:type = 1 AND LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')))"
        + " OR (:type = 2 AND LOWER(d.content) LIKE LOWER(CONCAT('%', :keyword, '%')))"
        + " OR (:type = 3 AND LOWER(d.member.nickName) LIKE LOWER(CONCAT('%', :keyword, '%'))) )"
        + " AND d.state = true ORDER BY d.createdAt DESC ")
    Page<Discussion> searchByType(
        @Param("type") int type,
        @Param("keyword") String keyword,
        Pageable pageable);

    Long countAllByStateIsTrueAndMember(Member member);

    @Query(value = "SELECT SUM(d.participantCount) FROM Discussion d WHERE d.member = :member AND d.state = true")
    Long sumParticipantCountByMember(@Param("member") Member member);
}
