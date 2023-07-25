package com.example.mssaem_backend.domain.search;

import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.search.dto.SearchRequestDto.SearchInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SearchService {
  private final SearchRepository searchRepository;

  public void insertKeywords(Member member, SearchInfo searchInfo) {
    Search search = new Search(searchInfo.getKeyword(), member);
    if(member != null){
      searchRepository.findAllByMember(member);
    }
  }

}
