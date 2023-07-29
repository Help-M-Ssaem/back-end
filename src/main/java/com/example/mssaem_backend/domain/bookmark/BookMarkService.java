package com.example.mssaem_backend.domain.bookmark;

import com.example.mssaem_backend.domain.bookmark.dto.BookMarkResponseDto.BookMarkInfo;
import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.Member;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BookMarkService {

    private final BookMarkRepository bookMarkRepository;

    @Transactional
    public Boolean updateBookMark(Member member, MbtiEnum mbti) {
        //해당 bookmark가 member, mbti 에 대해 존재하는 지 확인
        if (bookMarkRepository.existsBookMarkByMemberAndMbti(member, mbti)) {
            //존재한다면 해당 즐겨찾기 상태 변경
            BookMark bookMark = bookMarkRepository.findByMemberAndMbti(member, mbti);
            bookMark.updateBookMark();
        } else {
            //존재하지 않는다면 새로운 즐겨찾기 추가
            bookMarkRepository.save(new BookMark(mbti, member));
        }
        return bookMarkRepository.findByMemberAndMbti(member, mbti).nowBookmarkState();
    }

    //즐겨찾기 목록 조회
    public List<BookMarkInfo> getBookMarkList(Member member) {
        List<BookMark> bookMarkList = bookMarkRepository.findAllByStateIsTrueAndMember(member);

        List<BookMarkInfo> bookMarkInfoList = new ArrayList<>();
        for (BookMark bookMark : bookMarkList) {
            bookMarkInfoList.add(new BookMarkInfo(
                Collections.singletonList(bookMark.getMbti())
                )
            );
        }
        return bookMarkInfoList;
    }
}
