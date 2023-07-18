package com.example.mssaem_backend.domain.badge;

import com.example.mssaem_backend.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BadgeService {

    private final BadgeRepository badgeRepository;

    //멤버의 대표 뱃지 가져오기
    public String findRepresentativeBadgeByMember(Member member) {
        return badgeRepository.findBadgeByMemberAndState(member, true).orElse(new Badge())
            .getName();
    }
}
