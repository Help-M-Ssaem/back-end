package com.example.mssaem_backend.domain.board.dto;

import com.example.mssaem_backend.domain.board.Board;
import com.example.mssaem_backend.domain.discussion.Discussion;
import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.domain.worryboard.WorryBoard;
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
        private String boardContent;
        private Long discussionId;
        private String discussionTitle;
        private String discussionContent;
        private Long worryBoardId;
        private String worryBoardTitle;
        private String worryBoardContent;

        @Builder
        public ThreeHotInfo(Board board, Discussion discussion, WorryBoard worryBoard) {
            this.boardId = board != null ? board.getId() : null;
            this.boardTitle = board != null ? board.getTitle() : null;
            this.boardContent = board != null ? board.getContent() : null;
            this.discussionId = discussion != null ? discussion.getId() : null;
            this.discussionTitle = discussion != null ? discussion.getTitle() : null;
            this.discussionContent = discussion != null ? discussion.getContent() : null;
            this.worryBoardId = worryBoard != null ? worryBoard.getId() : null;
            this.worryBoardTitle = worryBoard != null ? worryBoard.getTitle() : null;
            this.worryBoardContent = worryBoard != null ? worryBoard.getContent() : null;
        }
    }

}
