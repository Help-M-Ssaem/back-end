package com.example.mssaem_backend.domain.discussioncomment;

import com.example.mssaem_backend.domain.discussion.Discussion;
import com.example.mssaem_backend.domain.member.Member;
import java.util.List;
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

    @Query(value = "SELECT dc FROM DiscussionComment dc JOIN FETCH dc.member WHERE dc.discussion.id = :id ORDER BY dc.createdAt ASC", countQuery = "SELECT count(dc) FROM DiscussionComment dc")
    Page<DiscussionComment> findAllByDiscussionId(@Param("id") Long id, Pageable pageable);

    DiscussionComment findByIdAndDiscussionIdAndStateIsTrue(Long commentId, Long discussionId);

    List<DiscussionComment> findAllByDiscussion(Discussion discussion);

    void deleteAllByDiscussionId(Long postId);
}