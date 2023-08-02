package com.example.mssaem_backend.domain.discussioncomment;

import com.example.mssaem_backend.domain.discussion.Discussion;
import com.example.mssaem_backend.domain.member.Member;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiscussionCommentRepository extends JpaRepository<DiscussionComment, Long> {

    Long countByDiscussionAndStateTrue(Discussion discussion);

    Long countAllByStateIsTrueAndMember(@Param("member") Member member);

    Optional<DiscussionComment> findByIdAndStateIsTrue(Long id);

    @Query(value = "select dc from DiscussionComment dc join fetch dc.member where dc.discussion.id = :id", countQuery = "select count(dc) from DiscussionComment dc")
    Page<DiscussionComment> findAllByDiscussionId(@Param("id") Long id, Pageable pageable);

    Boolean existsDiscussionCommentById(Long id);

    DiscussionComment findByIdAndDiscussionIdAndStateIsTrue(Long commentId, Long discussionId);
}