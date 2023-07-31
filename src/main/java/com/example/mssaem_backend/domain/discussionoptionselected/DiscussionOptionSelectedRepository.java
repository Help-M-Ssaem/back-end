package com.example.mssaem_backend.domain.discussionoptionselected;

import com.example.mssaem_backend.domain.discussion.Discussion;
import com.example.mssaem_backend.domain.discussionoption.DiscussionOption;
import com.example.mssaem_backend.domain.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiscussionOptionSelectedRepository extends
    JpaRepository<DiscussionOptionSelected, Long> {

    DiscussionOptionSelected findDiscussionOptionSelectedByMemberAndDiscussionOptionAndStateFalse(
        Member member, DiscussionOption discussionOption);

    DiscussionOptionSelected findDiscussionOptionSelectedByMemberAndDiscussionOptionAndStateTrue(
        Member member, DiscussionOption beforeDiscussionOption);

    @Query("SELECT dos.discussionOption FROM DiscussionOptionSelected dos "
        + "WHERE dos.discussionOption.discussion = :discussion " + "AND dos.member = :member "
        + "AND dos.state = true")
    DiscussionOption findDiscussionOptionByDiscussionAndMemberAndStateTrue(
        @Param("discussion") Discussion discussion, @Param("member") Member member);

    @Query("SELECT dos FROM DiscussionOptionSelected dos "
        + "JOIN dos.discussionOption option "
        + "WHERE dos.member = :member "
        + "AND option.discussion = :discussion ORDER BY option.id ASC")
    List<DiscussionOptionSelected> findAllByMemberAndDiscussionOrderByOptionIdAsc(@Param("member") Member member, @Param("discussion") Discussion discussion);
}