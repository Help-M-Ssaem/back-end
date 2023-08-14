package com.example.mssaem_backend.domain.report;

import lombok.Getter;

public enum ReportReason {
    PROMOTIONAL_POST("부적절한 홍보 게시글"),
    LEWDNESS("음란성 또는 청소년에게 부적합한 내용"),
    HATRED("증오 또는 악의적인 콘텐츠"),
    TORMENT("괴롭힘 또는 폭력"),
    INFRINGEMENT("권리 침해"),
    ETC("기타");

    @Getter
    private final String name;

    ReportReason(String name) {
        this.name = name;
    }
}
