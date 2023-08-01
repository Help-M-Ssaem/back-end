package com.example.mssaem_backend.domain.boardcommentlike;

import com.example.mssaem_backend.domain.boardcomment.BoardComment;
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
public class BoardCommentLike extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean state = true;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private BoardComment boardComment;

    public BoardCommentLike(BoardComment boardComment, Member member) {
        this.boardComment = boardComment;
        this.member = member;
        this.boardComment.increaseLikeCount();
    }

    public void updateBoardCommentLike() {
        this.state = !this.state;
        if (!this.state) {
            this.boardComment.decreaseLikeCount();
        } else {
            this.boardComment.increaseLikeCount();
        }
    }

    public Boolean nowBoardCommentLikeState() {
        return this.state;
    }
}
