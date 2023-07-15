package com.example.mssaem_backend.domain.discussion;

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
public class Discussion extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String content;

    @NotNull
    private String title;

    @ColumnDefault("0")
    private Long participants; // 참여자 수

    @ColumnDefault("0")
    private Long report;

    @ColumnDefault("0")
    private Long hits;

    @ColumnDefault("true")
    private boolean state; //true : 삭제아님, false : 삭제

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}
