package com.example.mssaem_backend.domain.worryboard.dto;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class WorryBoardResponseDto {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GetWorryRes {
    private  String title;
    private  String content;
    private  MbtiEnum memberMbti;
    private  MbtiEnum targetMbti;
    private  LocalDateTime createdDate;
    private  List<String> imgList;

    public GetWorryRes(WorryBoard worryBoard, Member member,List<String> imgList) {
      this.title = worryBoard.getTitle();
      this.content = worryBoard.getContent();
      this.memberMbti = member.getMbti();
      this.targetMbti = worryBoard.getTargetMbti();
      this.createdDate = worryBoard.getCreatedAt();
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
    private LocalDateTime createdDate;
    private String imgUrl;

    public GetWorriesRes(WorryBoard worryBoard, String imgUrl) {
      this.title = worryBoard.getTitle();
      this.content = worryBoard.getContent();
      this.memberMbti = worryBoard.getMember().getMbti();
      this.targetMbti = worryBoard.getTargetMbti();
      this.createdDate = worryBoard.getCreatedAt();
      this.imgUrl = imgUrl;
    }
  }
}

