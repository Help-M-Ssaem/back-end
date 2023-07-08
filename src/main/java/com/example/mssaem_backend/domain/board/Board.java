package com.example.mssaem_backend.domain.board;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;


@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Board extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ColumnDefault("0")
    private Long recommendation;

    @NotNull
    private String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MbtiEnum mbti;

    @ColumnDefault("0")
    private Long report;

    @ColumnDefault("0")
    private Long heats;

    @ColumnDefault("true")
    private boolean state; //true : 삭제아님, false : 삭제

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}
