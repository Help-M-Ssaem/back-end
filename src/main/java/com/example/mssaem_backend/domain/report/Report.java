package com.example.mssaem_backend.domain.report;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long resourceId; // 신고 대상 id

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReportTarget reportTarget; // 신고 대상 type

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReportReason reportReason; // 신고 사유

    @Nullable
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder
    public Report(Long resourceId, ReportTarget reportTarget, ReportReason reportReason, String content, Member member) {
        this.resourceId = resourceId;
        this.reportTarget = reportTarget;
        this.reportReason = reportReason;
        this.content = content;
        this.member = member;
    }
}
