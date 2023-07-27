package com.example.mssaem_backend.domain.board;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;


@DynamicInsert
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ColumnDefault("0")
    private Long likeCount;

    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MbtiEnum mbti;

    @ColumnDefault("0")
    private Long report;

    @ColumnDefault("0")
    private Long hits;

    private boolean state = true; //true : 삭제아님, false : 삭제

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String thumbnail;
    @ColumnDefault("0")
    private Long commentCount;

    @Builder
    public Board(String title, String content, MbtiEnum mbti, Member member) {
        this.title = title;
        this.content = content;
        this.mbti = mbti;
        this.member = member;
    }

    public void modifyBoard(String title, String content, MbtiEnum mbti) {
        this.title = title != null ? title : this.title;
        this.content = content != null ? content : this.content;
        this.mbti = mbti != null ? mbti : this.mbti;
    }

    public void deleteBoard() {
        this.state = false;
    }
}
