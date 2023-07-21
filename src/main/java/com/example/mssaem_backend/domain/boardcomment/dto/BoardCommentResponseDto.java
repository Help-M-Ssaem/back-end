package com.example.mssaem_backend.domain.boardcomment.dto;

import com.example.mssaem_backend.domain.boardcomment.BoardComment;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BoardCommentResponseDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BoardCommentSimpleInfo {

        private String Content;
        private Long likeCount;
        private Integer depth;
        private Integer parentId;
        private Integer orders;
        private boolean state;
        String createdAt;
        private MemberSimpleInfo memberSimpleInfo;

        public BoardCommentSimpleInfo(MemberSimpleInfo memberSimpleInfo, BoardComment boardComment,
            String createdAt) {
            this.Content = boardComment.getContent();
            this.likeCount = boardComment.getLikeCount();
            this.depth = boardComment.getDepth();
            this.parentId = boardComment.getParentId();
            this.orders = boardComment.getOrders();
            this.state = boardComment.isState();
            this.memberSimpleInfo = memberSimpleInfo;
            this.createdAt = createdAt;
        }
    }

}
