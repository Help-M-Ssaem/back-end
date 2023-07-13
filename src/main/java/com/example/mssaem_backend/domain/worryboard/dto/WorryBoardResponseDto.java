package com.example.mssaem_backend.domain.worryboard.dto;

import static com.example.mssaem_backend.global.common.Time.calculateTime;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class WorryBoardResponseDto {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GetWorryRes {

    private String nickName;
    private String profileImgUrl;
    private MbtiEnum memberMbti;
    private MbtiEnum targetMbti;
    private String title;
    private String content;
    private String createdAt;
    private List<String> imgList;


    public GetWorryRes(WorryBoard worryBoard, List<String> imgList) {
      this.nickName = worryBoard.getMember().getNickName();
      this.profileImgUrl = worryBoard.getMember().getProfileImageUrl();
      this.memberMbti = worryBoard.getMember().getMbti();
      this.targetMbti = worryBoard.getTargetMbti();
      this.title = worryBoard.getTitle();
      this.content = worryBoard.getContent();
      this.createdAt = calculateTime(worryBoard.getCreatedAt(), 2);
      this.imgList = imgList;
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

    public GetWorriesRes(WorryBoard worryBoard, String imgUrl) {
      this.title = worryBoard.getTitle();
      this.content = worryBoard.getContent();
      this.memberMbti = worryBoard.getMember().getMbti();
      this.targetMbti = worryBoard.getTargetMbti();
      this.createdDate = calculateTime(worryBoard.getCreatedAt(), 3);
      this.imgUrl = imgUrl;
    }
  }

}

