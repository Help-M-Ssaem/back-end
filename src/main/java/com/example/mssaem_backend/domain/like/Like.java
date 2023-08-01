package com.example.mssaem_backend.domain.like;

import com.example.mssaem_backend.domain.board.Board;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.common.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@DynamicInsert
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "likes")
public class Like extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean state = true; //true : 좋아요, false : 좋아요 아님

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    public Like(Board board, Member member) {
        this.board = board;
        this.member = member;
        this.board.increaseLikeCount();
    }

    public void updateBoardLike() {
        this.state = !this.state;
        if (!this.state) {
            this.board.decreaseLikeCount();
        } else {
            this.board.increaseLikeCount();
        }
    }

    public Boolean nowBoardLikeState() {
        return this.state;
    }
}
