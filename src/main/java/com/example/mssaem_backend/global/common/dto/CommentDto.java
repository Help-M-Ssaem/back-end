package com.example.mssaem_backend.global.common.dto;

import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.global.common.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommentDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostCommentReq {

        private String content;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetCommentsRes {

        private Long commentId;
        private String content;
        private Long likeCount;
        private Long parentId;
        private String createdAt;
        private Boolean isLiked; // 댓글 좋아요 눌렀는지 확인
        private Boolean isEditAllowed; //댓글 또는 신고를 위한 내 댓글인지 확인
        private MemberSimpleInfo memberSimpleInfo;

        @Builder
        public GetCommentsRes(Comment comment, MemberSimpleInfo memberSimpleInfo, String createdAt,
            Boolean isEditAllowed, Boolean isLiked) {
            this.commentId = comment.getId();
            this.content = comment.getContent();
            this.likeCount = comment.getLikeCount();
            this.parentId = comment.getParentId();
            this.memberSimpleInfo = memberSimpleInfo;
            this.createdAt = createdAt;
            this.isEditAllowed = isEditAllowed;
            this.isLiked = isLiked;
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetCommentsByMemberRes {

        private Long PostId;
        private Long commentId;
        private String content;
        private Long likeCount;
        private Long parentId;
        private String createdAt;
        private Boolean isLiked; // 댓글 좋아요 눌렀는지 확인
        private Boolean isEditAllowed; //삭제 또는 신고를 위한 내 댓글인지 확인
        private MemberSimpleInfo memberSimpleInfo;

        @Builder
        public GetCommentsByMemberRes(Long postId, Comment comment,
            MemberSimpleInfo memberSimpleInfo, String createdAt, Boolean isEditAllowed,
            Boolean isLiked) {
            this.PostId = postId;
            this.commentId = comment.getId();
            this.content = comment.getContent();
            this.likeCount = comment.getLikeCount();
            this.parentId = comment.getParentId();
            this.memberSimpleInfo = memberSimpleInfo;
            this.createdAt = createdAt;
            this.isEditAllowed = isEditAllowed;
            this.isLiked = isLiked;
        }
    }
}
