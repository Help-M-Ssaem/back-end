package com.example.mssaem_backend.domain.worryboard;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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

    @ColumnDefault("false")
    private boolean state; //true : 해결, false : 해결 안함

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member; //이 고민글을 신청한 유저

    @ManyToOne(fetch = FetchType.LAZY)
    private Member solveMember; //이 고민글을 해결해준 유저

    @ColumnDefault("0")
    private Long hits;

}
