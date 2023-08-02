package com.example.mssaem_backend.domain.discussioncommentlike;

import com.example.mssaem_backend.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscussionCommentLikeRepository extends
    JpaRepository<DiscussionCommentLike, Long> {

    Boolean existsDiscussionCommentLikeByMemberAndStateIsTrueAndDiscussionCommentId(Member member, Long id);
}
