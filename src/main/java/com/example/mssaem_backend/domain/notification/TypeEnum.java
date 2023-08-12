package com.example.mssaem_backend.domain.notification;

import lombok.Getter;

public enum TypeEnum {
    BOARD_COMMENT("내 게시물에 댓글이 달렸어요."),
    HOT_BOARD("내 게시글이 HOT한 게시글이 되었어요."),

    BOARD_REPLY_OF_COMMENT("내 댓글에 대댓글이 달렸어요."),
    DISCUSSION_COMMENT("내 토론에 댓글이 달렸어요."),
    HOT_DISCUSSION("내 토론이 HOT한 토론이 되었어요."),
    DISCUSSION_REPLY_OF_COMMENT("내 댓글에 대댓글이 달렸어요."),
    HOT_TEACHER("내가 인기 M쌤이 되었어요."),
    CHAT("채팅을 시작했어요.");

    @Getter
    private final String name;

    TypeEnum(String name) {
        this.name = name;
    }
}
