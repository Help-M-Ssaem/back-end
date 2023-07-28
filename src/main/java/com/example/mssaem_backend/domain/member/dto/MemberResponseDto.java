package com.example.mssaem_backend.domain.member.dto;

import com.example.mssaem_backend.domain.badge.BadgeResponse.BadgeInfo;
import com.example.mssaem_backend.domain.board.dto.BoardResponseDto.BoardHistory;
import com.example.mssaem_backend.domain.discussion.dto.DiscussionResponseDto.DiscussionHistory;
import com.example.mssaem_backend.domain.evaluation.dto.EvaluationResultDto.EvaluationCount;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardResponseDto.WorryBoardHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class MemberResponseDto {

    @Getter
    @AllArgsConstructor
    public static class TokenInfo {

        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberSimpleInfo {

        private Long id;
        private String nickName;
        private String mbti;
        private String badge;
        private String profileImgUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class CheckNickNameRes {

        private boolean isUsed;
    }

    @Getter
    @AllArgsConstructor
    public static class TeacherInfo {

        private Long id;
        private String nickName;
        private String mbti;
        private String badge;
        private String profileImgUrl;
        private String introduction;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberProfileInfo {
        private TeacherInfo teacherInfo;
        private List<BadgeInfo> badgeInfos;
        private EvaluationCount evaluationCount;
        private BoardHistory boardHistory;
        private DiscussionHistory DiscussionHistory;
        private WorryBoardHistory worryBoardHistory;
    }
}