package com.example.mssaem_backend.domain.report.dto;

import com.example.mssaem_backend.domain.report.ReportType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReportRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportReq {

        Long resourceId; // 신고 대상 id
        ReportType reportType; // 신고 대상 타입
        String content; // 신고 사유
    }
}
