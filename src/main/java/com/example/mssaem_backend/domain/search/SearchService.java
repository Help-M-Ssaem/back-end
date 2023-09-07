package com.example.mssaem_backend.domain.search;

import com.example.mssaem_backend.domain.board.Board;
import com.example.mssaem_backend.domain.board.BoardRepository;
import com.example.mssaem_backend.domain.board.dto.BoardResponseDto.BoardSimpleInfo;
import com.example.mssaem_backend.domain.discussion.Discussion;
import com.example.mssaem_backend.domain.discussion.DiscussionRepository;
import com.example.mssaem_backend.domain.discussion.DiscussionService;
import com.example.mssaem_backend.domain.discussion.dto.DiscussionResponseDto.DiscussionSimpleInfo;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.domain.search.dto.SearchRequestDto.SearchInfo;
import com.example.mssaem_backend.domain.search.dto.SearchResponseDto.SearchPopular;
import com.example.mssaem_backend.domain.search.dto.SearchResponseDto.SearchRecent;
import com.example.mssaem_backend.domain.search.dto.SearchResponseDto.SearchRes;
import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import com.example.mssaem_backend.domain.worryboard.WorryBoardRepository;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardResponseDto.GetWorriesSearchRes;
import com.example.mssaem_backend.global.common.Time;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SearchService {

  private final SearchRepository searchRepository;
  private final BoardRepository boardRepository;
  private final DiscussionRepository discussionRepository;
  private final WorryBoardRepository worryBoardRepository;
  private final DiscussionService discussionService;
  private final SearchCustomRepository searchCustomRepository;

  /**
   * 최근 검색어 저장
   */
  @Transactional
  public void insertKeywords(Member member, SearchInfo searchInfo) {
    Search search = new Search(searchInfo.getKeyword(), member);
    Search byKeywordAndMember = searchRepository.findByKeywordAndMember(search.getKeyword(),
        member);
    // 최근 검색어에 검색어가 있는 경우
    if (byKeywordAndMember != null) {
      byKeywordAndMember.setKeyword(searchInfo.getKeyword());
    }
    // 최근 검색에어 검색어가 없는 경우
    else {
      List<Search> allByMember = searchRepository.findAllByMemberOrderByUpdatedAtDesc(member);
      // 최근 검색어가 5개인 경우
      if (allByMember.size() >= 5) {
        // 제일 update 나중에 된 것을 update
        allByMember.get(allByMember.size() - 1).setKeyword(search.getKeyword());
      } else {
        searchRepository.save(search);
      }
    }

  }

  /**
   * 전체 게시판, 고민 게시판, 토론 게시판 조회
   */
  public SearchRes selectEverySearch(SearchInfo searchInfo, Member member) {
    PageRequest pageRequest = PageRequest.of(0, 5);

    // Board 5개 가져오기
    Page<Board> pageBoards = boardRepository.findByKeyword(searchInfo.getKeyword(), pageRequest);
    List<BoardSimpleInfo> boardSimpleResults = pageBoards.stream()
        .map(b -> new BoardSimpleInfo(b,
            new MemberSimpleInfo(b.getMember()),
            Time.calculateTime(b.getCreatedAt(), 3)))
        .collect(Collectors.toList());

    // 고민글 5개 가져오기
    Page<WorryBoard> pageWorryBoards = worryBoardRepository.findByKeyword(searchInfo.getKeyword(),
        pageRequest);
    List<GetWorriesSearchRes> worrySimpleResults = pageWorryBoards.stream()
        .map(wb -> new GetWorriesSearchRes(wb, wb.getThumbnail(),
            Time.calculateTime(wb.getCreatedAt(), 2)))
        .collect(Collectors.toList());

    // 토른글 5개 가져오기
    Page<Discussion> pageDiscussions = discussionRepository.findByKeyword(searchInfo.getKeyword(),
        pageRequest);
    List<DiscussionSimpleInfo> discussionSimpleInfos = discussionService.setDiscussionSimpleInfo(
        member, pageDiscussions.stream().toList(), 3);
    return new SearchRes(
        new PageResponseDto<>(pageBoards.getNumber(), pageBoards.getTotalPages(),
            boardSimpleResults),
        new PageResponseDto<>(pageWorryBoards.getNumber(), pageWorryBoards.getTotalPages(),
            worrySimpleResults),
        new PageResponseDto<>(pageDiscussions.getNumber(), pageDiscussions.getTotalPages(),
            discussionSimpleInfos));
  }

  public void insertRedisSearch(SearchInfo searchInfo) {
    searchCustomRepository.insertRedis(searchInfo);
  }

  public List<SearchPopular> selectAllSearch() {
    return searchCustomRepository.selectAllSearch();
  }

  public List<SearchRecent> selectRecentSearch(Member member) {
    return searchRepository.findAllByMemberOrderByUpdatedAtDesc(member).stream()
        .map(SearchRecent::new).collect(
            Collectors.toList());
  }

  public List<SearchPopular> selectPopularSearch() {
    return searchCustomRepository.selectAllPopular();
  }

}
