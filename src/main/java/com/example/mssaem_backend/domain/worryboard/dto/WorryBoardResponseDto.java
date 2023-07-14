package com.example.mssaem_backend.domain.worryboard.dto;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class WorryBoardResponseDto {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GetWorryRes {
    private MemberSimpleInfo memberSimpleInfo;
    private Long worryBoardId;
    private MbtiEnum targetMbti;
    private String title;
    private String content;
    private String createdAt;
    private List<String> imgList;
    private Boolean isAllowed;

    @Builder
    public GetWorryRes(WorryBoard worryBoard, List<String> imgList,
        MemberSimpleInfo memberSimpleInfo, String createdAt, Boolean isAllowed) {
      this.worryBoardId = worryBoard.getId();
      this.memberSimpleInfo = memberSimpleInfo;
      this.targetMbti = worryBoard.getTargetMbti();
      this.title = worryBoard.getTitle();
      this.content = worryBoard.getContent();
      this.createdAt = createdAt;
      this.imgList = imgList;
      this.isAllowed = isAllowed;
    }
  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class GetWorriesRes {

    private String title;
    private String content;
    private MbtiEnum memberMbti;
    private MbtiEnum targetMbti;
    private String createdDate;
    private String imgUrl;

    @Builder
    public GetWorriesRes(WorryBoard worryBoard, String imgUrl, String createdAt) {
      this.title = worryBoard.getTitle();
      this.content = worryBoard.getContent();
      this.memberMbti = worryBoard.getMember().getMbti();
      this.targetMbti = worryBoard.getTargetMbti();
      this.createdDate = createdAt;
      this.imgUrl = imgUrl;
    }
  }

}
