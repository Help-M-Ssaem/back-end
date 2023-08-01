package com.example.mssaem_backend.domain.discussioncomment;

import com.example.mssaem_backend.domain.discussion.Discussion;
import com.example.mssaem_backend.domain.member.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface DiscussionCommentRepository extends JpaRepository<DiscussionComment, Long> {

    Long countByDiscussionAndStateTrue(Discussion discussion);

    Long countAllByStateIsTrueAndMember(@Param("member") Member member);

    Optional<DiscussionComment> findByIdAndStateIsTrue(Long id);
}
