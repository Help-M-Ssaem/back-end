package com.example.mssaem_backend.global.common;

import com.example.mssaem_backend.domain.board.Board;
import com.example.mssaem_backend.domain.discussion.Discussion;
import com.example.mssaem_backend.domain.member.Member;
import java.time.LocalDateTime;

public interface Comment {
    Long getId();
    Integer getParentId();
    String getContent();
    Long getLikeCount();
    LocalDateTime getCreatedAt();
    Member getMember();
    Discussion getDiscussion();
    Board getBoard();

    void setParentComment(Integer commentId);

    void deleteComment();
}