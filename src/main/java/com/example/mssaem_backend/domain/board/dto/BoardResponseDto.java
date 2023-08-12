package com.example.mssaem_backend.domain.board.dto;

import com.example.mssaem_backend.domain.board.Board;
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

        public BoardSimpleInfo(Board board, MemberSimpleInfo memberSimpleInfo, String createdAt) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.imgUrl = board.getThumbnail();
            this.boardMbti = board.getMbti();
            this.likeCount = board.getLikeCount();
            this.commentCount = board.getCommentCount();
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
        private Boolean isAllowed; //게시글 수정 삭제 권한 확인
        private Boolean isLiked; //게시글 좋아요 눌렀는지 확인
        private MbtiEnum boardMbti; //게시글 MBTI

        @Builder
        public GetBoardRes(MemberSimpleInfo memberSimpleInfo, Board board, List<String> imgUrlList,
            String createdAt, Long commentCount, Boolean isAllowed, Boolean isLiked) {
            this.memberSimpleInfo = memberSimpleInfo;
            this.boardId = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.imgUrlList = imgUrlList;
            this.createdAt = createdAt;
            this.likeCount = board.getLikeCount();
            this.commentCount = commentCount;
            this.isAllowed = isAllowed;
            this.isLiked = isLiked;
            this.boardMbti = board.getMbti();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardHistory {

        private Long boardCount;         // 전체 게시글 수
        private Long boardCommentCount;  // 전체 게시글 댓글 수
        private Long likeAllCount;       // 받은 좋아요의 수
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardList {

        private Long INFJ;
        private Long INFP;
        private Long ISFJ;
        private Long ISFP;
        private Long ISTP;
        private Long ISTJ;
        private Long INTP;
        private Long INTJ;
        private Long ENTP;
        private Long ESTJ;
        private Long ESTP;
        private Long ENFP;
        private Long ESFJ;
        private Long ENTJ;
        private Long ENFJ;
        private Long ESFP;
        private Long boardCount; //전체 게시글 수

        public void setINFJ(Long count) {
            this.INFJ = count;
        }

        public void setINFP(Long count) {
            this.INFP = count;
        }

        public void setISFJ(Long count) {
            this.ISFJ = count;
        }

        public void setISFP(Long count) {
            this.ISFP = count;
        }

        public void setISTP(Long count) {
            this.ISTP = count;
        }

        public void setISTJ(Long count) {
            this.ISTJ = count;
        }

        public void setINTP(Long count) {
            this.INTP = count;
        }

        public void setINTJ(Long count) {
            this.INTJ = count;
        }

        public void setENTP(Long count) {
            this.ENTP = count;
        }

        public void setESTJ(Long count) {
            this.ESTJ = count;
        }

        public void setESTP(Long count) {
            this.ESTP = count;
        }

        public void setENFP(Long count) {
            this.ENFP = count;
        }

        public void setESFJ(Long count) {
            this.ESFJ = count;
        }

        public void setENTJ(Long count) {
            this.ENTJ = count;
        }

        public void setENFJ(Long count) {
            this.ENFJ = count;
        }

        public void setESFP(Long count) {
            this.ESFP = count;
        }

        public void setBoardCount(Long count) {
            this.boardCount = count;
        }
    }
}
