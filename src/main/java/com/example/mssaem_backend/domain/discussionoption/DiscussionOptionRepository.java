package com.example.mssaem_backend.domain.discussionoption;

import com.example.mssaem_backend.domain.discussion.Discussion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscussionOptionRepository extends JpaRepository<DiscussionOption, Long> {

    List<DiscussionOption> findDiscussionOptionByDiscussion(Discussion discussion);
}
