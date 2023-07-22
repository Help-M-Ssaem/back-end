package com.example.mssaem_backend.domain.board.dto;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
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
    public static class ThreeHotInfo {

        private Long boardId;
        private String boardTitle;
        private Long discussionId;
        private String discussionTitle;
        private Long worryBoardId;
        private String worryBoardTitle;

        @Builder
        public ThreeHotInfo(Long boardId, String boardTitle, Long discussionId,
            String discussionTitle, Long worryBoardId, String worryBoardTitle) {
            this.boardId = boardId;
            this.boardTitle =
                boardTitle != null && boardTitle.length() >= 35
                    ? boardTitle.substring(0, 35)
                    : boardTitle;
            this.discussionId = discussionId;
            this.discussionTitle =
                discussionTitle != null && discussionTitle.length() >= 35
                    ? discussionTitle.substring(0, 35)
                    : discussionTitle;
            this.worryBoardId = worryBoardId;
            this.worryBoardTitle =
                worryBoardTitle != null && worryBoardTitle.length() >= 35
                    ? worryBoardTitle.substring(0, 35)
                    : worryBoardTitle;
        }
    }

}
