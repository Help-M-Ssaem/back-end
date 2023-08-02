package com.example.mssaem_backend.domain.discussioncomment;

import com.example.mssaem_backend.domain.discussion.Discussion;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@DynamicInsert
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DiscussionComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String content;

    @ColumnDefault("0")
    private Long likeCount;

    @ColumnDefault("0")
    private Integer report;

    @ColumnDefault("0")
    private Integer parentId; //댓글 : 0, 대 댓글 : 자신의 부모 댓글 id

    @ColumnDefault("0")
    private Integer orders; //대댓글의 순서

    private boolean state = true; //true : 존재, false : 삭제

    @ManyToOne(fetch = FetchType.LAZY)
    private Discussion discussion;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public DiscussionComment(String content, Member member, Discussion discussion, Integer parentId) {
        this.content = content;
        this.member = member;
        this.discussion = discussion;
        this.parentId = parentId;
    }

    public Integer increaseReport() {
        return this.report++;
    }

    public void updateState() {
        this.state = false;
    }

    public void deleteDiscussionComment() {
        this.content = "삭제된 댓글입니다.";
        this.state = false;
    }
}
