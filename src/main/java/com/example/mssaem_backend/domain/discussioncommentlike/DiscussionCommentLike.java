package com.example.mssaem_backend.domain.discussioncommentlike;

import com.example.mssaem_backend.domain.discussioncomment.DiscussionComment;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.common.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DiscussionCommentLike extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean state = true;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private DiscussionComment discussionComment;

    public DiscussionCommentLike(DiscussionComment discussionComment, Member member) {
        this.discussionComment = discussionComment;
        this.member = member;
        this.discussionComment.increaseLikeCount();
    }

    public void updateDiscussionCommentLike() {
        this.state = !this.state;
        if (!this.state) {
            this.discussionComment.decreaseLikeCount();
        } else {
            this.discussionComment.increaseLikeCount();
        }
    }

    public Boolean nowDiscussionCommentLikeState() {
        return this.state;
    }
}
