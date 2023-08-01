package com.example.mssaem_backend.domain.report;

import lombok.Getter;

public enum ReportType {
    BOARD("게시물"),
    DISCUSSION("토론글"),
    WORRY("고민글"),
    MEMBER("유저"),
    BOARD_COMMENT("게시물 댓글"),
    DISCUSSION_COMMENT("토론글 댓글");

    @Getter
    private final String name;

    ReportType(String name) {
        this.name = name;
    }
}
