package com.example.mssaem_backend.domain.discussioncomment;

import com.example.mssaem_backend.domain.discussion.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscussionCommentRepository extends JpaRepository<DiscussionComment, Long> {

    Long countWithStateTrueByDiscussion(Discussion discussion);
}
