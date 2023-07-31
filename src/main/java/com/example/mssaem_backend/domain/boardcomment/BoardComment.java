package com.example.mssaem_backend.domain.boardcomment;

import com.example.mssaem_backend.domain.board.Board;
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
public class BoardComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String content;

    @ColumnDefault("0")
    private Long likeCount;

    @ColumnDefault("0")
    private Integer parentId; //댓글 : 0, 대 댓글 : 자신의 부모 댓글 id

    private boolean state = true; //true : 존재, false : 삭제

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public BoardComment(String content, Member member, Board board, Integer parentId) {
        this.content = content;
        this.member = member;
        this.board = board;
        this.parentId = parentId;
    }

    public void deleteBoardComment() {
        this.content = "삭제된 댓글입니다.";
        this.state = false;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount--;
    }
}
