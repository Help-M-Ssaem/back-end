package com.example.mssaem_backend.domain.discussion;

import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.common.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
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
public class Discussion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String content;

    @NotNull
    private String title;

    @ColumnDefault("0")
    private Long participantCount; // 참여자 수

    @ColumnDefault("0")
    private Long report;

    @ColumnDefault("0")
    private Long hits;

    private boolean state = true; //true : 존재, false : 삭제

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder
    public Discussion(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
    }

    @ColumnDefault("0")
    private Long commentCount;
}

    public void modifyDiscussion(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void deleteDiscussion() {
        this.state = false;
    }
}