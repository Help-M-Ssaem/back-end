package com.example.mssaem_backend.domain.discussionoptionselected;

import com.example.mssaem_backend.domain.discussionoption.DiscussionOption;
import com.example.mssaem_backend.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscussionOptionSelectedRepository extends
    JpaRepository<DiscussionOptionSelected, Long> {

    DiscussionOptionSelected findDiscussionOptionSelectedWithStateByMemberAndDiscussionOption(
        Member member, DiscussionOption discussionOption);
}