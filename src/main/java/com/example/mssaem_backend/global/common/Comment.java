package com.example.mssaem_backend.global.common;

import com.example.mssaem_backend.domain.board.Board;
import com.example.mssaem_backend.domain.discussion.Discussion;
import com.example.mssaem_backend.domain.member.Member;
import java.time.LocalDateTime;

public interface Comment {
    public Long getId();
    public Integer getParentId();
    public String getContent();
    public Long getLikeCount();
    public LocalDateTime getCreatedAt();
    public Member getMember();
    public Discussion getDiscussion();
    public Board getBoard();
}
