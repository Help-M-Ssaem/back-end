package com.example.mssaem_backend.domain.bookmark;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookMarkRepository extends JpaRepository<BookMark, Long> {

    BookMark findByMemberAndMbti(Member member, MbtiEnum mbtiEnum);

    Boolean existsBookMarkByMemberAndMbti(Member member, MbtiEnum mbtiEnum);

}
