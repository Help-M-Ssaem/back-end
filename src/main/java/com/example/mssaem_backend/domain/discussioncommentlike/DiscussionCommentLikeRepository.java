package com.example.mssaem_backend.domain.discussioncommentlike;

import com.example.mssaem_backend.domain.discussioncomment.DiscussionComment;
import com.example.mssaem_backend.domain.member.Member;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiscussionCommentLikeRepository extends
    JpaRepository<DiscussionCommentLike, Long> {

    Boolean existsDiscussionCommentLikeByMemberAndStateIsTrueAndDiscussionCommentId(Member member, Long id);

    @Query(value = "SELECT dc FROM DiscussionComment dc WHERE dc.discussion.id = :discussionId AND dc.likeCount >= 10 AND dc.state = true ORDER BY bc.likeCount DESC")
    Page<DiscussionComment> findDiscussionCommentsByDiscussionIdWithMoreThanTenDiscussionCommentLikeAndStateTrue(
        PageRequest pageRequest, @Param("discussionId") Long discussionId);
}
