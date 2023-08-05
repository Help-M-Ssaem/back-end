package com.example.mssaem_backend.domain.boardcomment.dto;

import com.example.mssaem_backend.domain.boardcomment.BoardComment;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BoardCommentResponseDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BoardCommentSimpleInfo {

        private Long commentId;
        private String Content;
        private Long likeCount;
        private Integer parentId;
        private String createdAt;
        private Boolean isLiked; // 댓글 좋아요 눌렀는지 확인
        private Boolean isAllowed; //삭제 또는 신고를 위한 내 댓글인지 확인
        private MemberSimpleInfo memberSimpleInfo;

        @Builder
        public BoardCommentSimpleInfo(MemberSimpleInfo memberSimpleInfo, BoardComment boardComment,
            String createdAt , Boolean isAllowed , Boolean isLiked) {
            this.commentId = boardComment.getId();
            this.Content = boardComment.getContent();
            this.likeCount = boardComment.getLikeCount();
            this.parentId = boardComment.getParentId();
            this.memberSimpleInfo = memberSimpleInfo;
            this.createdAt = createdAt;
            this.isAllowed = isAllowed;
            this.isLiked = isLiked;
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BoardCommentSimpleInfoByMember {

        private Long boardId;
        private Long commentId;
        private String Content;
        private Long likeCount;
        private Integer parentId;
        private String createdAt;
        private MemberSimpleInfo memberSimpleInfo;

        @Builder
        public BoardCommentSimpleInfoByMember(MemberSimpleInfo memberSimpleInfo, BoardComment boardComment,
            String createdAt , Long boardId) {
            this.boardId = boardId;
            this.commentId = boardComment.getId();
            this.Content = boardComment.getContent();
            this.likeCount = boardComment.getLikeCount();
            this.parentId = boardComment.getParentId();
            this.memberSimpleInfo = memberSimpleInfo;
            this.createdAt = createdAt;
        }
    }

}
