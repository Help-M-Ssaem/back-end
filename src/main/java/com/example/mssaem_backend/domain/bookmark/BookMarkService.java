package com.example.mssaem_backend.domain.bookmark;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BookMarkService {

    private final BookMarkRepository bookMarkRepository;

    @Transactional
    public String updateBookMark(Member member, MbtiEnum mbti) {
        //해당 bookmark가 member, mbti 에 대해 존재하는 지 확인
        if(bookMarkRepository.existsBookMarkByMemberAndMbti(member, mbti)){
            //존재한다면 해당 즐겨찾기 상태 변경
            BookMark bookMark = bookMarkRepository.findByMemberAndMbti(member, mbti);
            bookMark.updateBookMark();
        } else if (!bookMarkRepository.existsBookMarkByMemberAndMbti(member, mbti)){
            //존재하지 않는다면 새로운 즐겨찾기 추가
            bookMarkRepository.save(new BookMark(mbti, member));
        }
        return "즐겨찾기 추가 완료";
    }

}
