package com.example.mssaem_backend.domain.discussioncomment.dto;

import com.example.mssaem_backend.domain.discussioncomment.DiscussionComment;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DiscussionCommentResponseDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiscussionCommentSimpleInfo {
        private Long commentId;
        private String content;
        private Long likeCount;
        private Integer parentId;
        private String createdAt;
        private Boolean isLiked; // 댓글 좋아요 눌렀는지 확인
        private Boolean isEditAllowed; //댓글 또는 신고를 위한 내 댓글인지 확인
        private MemberSimpleInfo memberSimpleInfo;

        @Builder
        public DiscussionCommentSimpleInfo(MemberSimpleInfo memberSimpleInfo, DiscussionComment discussionComment,
            String createdAt , Boolean isEditAllowed , Boolean isLiked) {
            this.commentId = discussionComment.getId();
            this.content = discussionComment.getContent();
            this.likeCount = discussionComment.getLikeCount();
            this.parentId = discussionComment.getParentId();
            this.memberSimpleInfo = memberSimpleInfo;
            this.createdAt = createdAt;
            this.isEditAllowed = isEditAllowed;
            this.isLiked = isLiked;
        }
    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiscussionCommentSimpleInfoByMember {

        private Long discussionId;
        private Long commentId;
        private String Content;
        private Long likeCount;
        private Integer parentId;
        private String createdAt;
        private Boolean isLiked; // 댓글 좋아요 눌렀는지 확인
        private Boolean isAllowed; //삭제 또는 신고를 위한 내 댓글인지 확인
        private MemberSimpleInfo memberSimpleInfo;

        @Builder
        public DiscussionCommentSimpleInfoByMember(MemberSimpleInfo memberSimpleInfo,
            DiscussionComment discussionComment, String createdAt, Long discussionId, Boolean isAllowed,
            Boolean isLiked) {
            this.discussionId = discussionId;
            this.commentId = discussionComment.getId();
            this.Content = discussionComment.getContent();
            this.likeCount = discussionComment.getLikeCount();
            this.parentId = discussionComment.getParentId();
            this.memberSimpleInfo = memberSimpleInfo;
            this.createdAt = createdAt;
            this.isAllowed = isAllowed;
            this.isLiked = isLiked;
        }
    }

}
