package com.example.mssaem_backend.domain.board.dto;

import com.example.mssaem_backend.domain.board.Board;
import com.example.mssaem_backend.domain.boardcomment.dto.BoardCommentResponseDto.BoardCommentSimpleInfo;
import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BoardResponseDto {

    @Getter
    @NoArgsConstructor
    public static class BoardSimpleInfo {

        private Long id;
        private String title;
        private String content;
        private String imgUrl;
        private MbtiEnum boardMbti;
        private Long likeCount;
        private Long commentCount;
        private String createdAt;
        private MemberSimpleInfo memberSimpleInfo;

        public BoardSimpleInfo(Long id, String title, String content, String imgUrl,
            MbtiEnum boardMbti,
            Long likeCount, Long commentCount, String createdAt,
            MemberSimpleInfo memberSimpleInfo) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.imgUrl = imgUrl == null ? "" : imgUrl;
            this.boardMbti = boardMbti;
            this.likeCount = likeCount;
            this.commentCount = commentCount;
            this.createdAt = createdAt;
            this.memberSimpleInfo = memberSimpleInfo;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetBoardRes {

        private MemberSimpleInfo memberSimpleInfo;
        private Long boardId;
        private String title;
        private String content;
        private List<String> imgUrlList;
        private String createdAt;
        private Long likeCount;
        private Long commentCount;
        private Boolean isAllowed;
        private List<BoardCommentSimpleInfo> boardCommentSimpleInfo;

        @Builder
        public GetBoardRes(MemberSimpleInfo memberSimpleInfo, Board board, List<String> imgUrlList,
            String createdAt, Long commentCount, Boolean isAllowed,
            List<BoardCommentSimpleInfo> boardCommentSimpleInfo) {
            this.memberSimpleInfo = memberSimpleInfo;
            this.boardId = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.imgUrlList = imgUrlList;
            this.createdAt = createdAt;
            this.likeCount = board.getLikeCount();
            this.commentCount = commentCount;
            this.isAllowed = isAllowed;
            this.boardCommentSimpleInfo = boardCommentSimpleInfo;
        }
    }
}
