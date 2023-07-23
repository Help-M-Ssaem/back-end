package com.example.mssaem_backend.domain.worryboard;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.common.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@DynamicInsert
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class WorryBoard extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MbtiEnum targetMbti;

    @ColumnDefault("0")
    private Long report;

    private boolean state = true; //고민글 삭제시 : false

    private Boolean isSolved = false; //true : 해결, false : 해결 안함

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member; //이 고민글을 신청한 유저

    @ManyToOne(fetch = FetchType.LAZY)
    private Member solveMember; //이 고민글을 해결해준 유저

    private LocalDateTime solvedAt; // 고민글이 해결된 시간

    @ColumnDefault("0")
    private Long hits;

    @Builder
    public WorryBoard(String title, String content, MbtiEnum targetMbti,
        Member member) {
        this.title = title;
        this.content = content;
        this.targetMbti = targetMbti;
        this.member = member;
    }

    public void solveWorryBoard(Member solveMember) {
        this.isSolved = true;
        this.solveMember = solveMember;
        this.solvedAt = LocalDateTime.now();
    }

    public void modifyWorryBoard(String title, String content, MbtiEnum targetMbti) {
        this.title = title;
        this.content = content;
        this.targetMbti = targetMbti;
    }

    public void deleteWorryBoard() {
        this.state = false;
    }
}
