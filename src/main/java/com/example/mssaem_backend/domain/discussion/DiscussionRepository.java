package com.example.mssaem_backend.domain.discussion;

import com.example.mssaem_backend.domain.member.Member;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {

    // HOT 토론 조회
    Page<Discussion> findDiscussionByParticipantCountGreaterThanEqualAndStateIsTrueOrderByCreatedAtDesc(
        Long participantCount, PageRequest pageRequest);

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


    @Query(value = "select d from Discussion d join fetch d.member "
        + "where( (lower(d.title) like lower(concat('%', :keyword, '%')))"
        + "or (lower(d.content) like lower(concat('%', :keyword, '%')))"
        + "or (lower(d.member.nickName) like lower(concat('%', :keyword, '%'))) )"
        + "and d.state = true order by d.createdAt desc",
        countQuery = "select count(d) from Discussion d")
    Page<Discussion> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Optional<Discussion> findByIdAndStateIsTrue(Long id);

    Long countAllByStateIsTrueAndMember(Member member);

    @Query(value = "SELECT SUM(d.participantCount) FROM Discussion d WHERE d.member = :member AND d.state = true")
    Optional<Long> sumParticipantCountByMember(@Param("member") Member member);

    @Query("SELECT d FROM Discussion d WHERE d.state = true AND (:discussionId IS NULL OR d.id <> :discussionId) order by d.createdAt desc ")
    Page<Discussion> findByStateTrueOrderByCreatedAtDesc(@Param("discussionId") Long discussionId,  PageRequest pageRequest);

    Page<Discussion> findAllByMemberAndStateIsTrueOrderByCreatedAtDesc(Member member,
        Pageable pageable);
}
