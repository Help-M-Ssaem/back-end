package com.example.mssaem_backend.domain.search.dto;

import com.example.mssaem_backend.domain.board.dto.BoardResponseDto.BoardSimpleInfo;
import com.example.mssaem_backend.domain.discussion.dto.DiscussionResponseDto.DiscussionSimpleInfo;
import com.example.mssaem_backend.domain.search.Search;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardResponseDto.GetWorriesSearchRes;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SearchResponseDto {

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class SearchRes {

    private PageResponseDto<List<BoardSimpleInfo>> boardSimpleInfos; // 전체 게시판
    private PageResponseDto<List<GetWorriesSearchRes>> getWorriesRes; // 고민 게시판
    private PageResponseDto<List<DiscussionSimpleInfo>> discussionSimpleInfo;
  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class SearchPopular {

    private String keyword;
    private Double score;
  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class SearchRecent {

    private String keyword;

    public SearchRecent(Search search){
      this.keyword = search.getKeyword();
    }
  }

}
