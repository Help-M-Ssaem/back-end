package com.example.mssaem_backend.domain.badge;

import com.example.mssaem_backend.domain.badge.dto.BadgeResponse;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.badge.dto.BadgeResponse.BadgeInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BadgeService {

    private final BadgeRepository badgeRepository;

    //멤버의 대표 뱃지 가져오기
    public String findRepresentativeBadgeByMember(Member member) {
        return badgeRepository.findNameMemberAndStateTrue(member).orElse(null);
    }

    public List<BadgeInfo> findAllBadge(Member member) {
        List<Badge> badges = badgeRepository.findAllByMember(member).orElse(null);
        List<BadgeInfo> result = new ArrayList<>();
        if (badges != null) {
            badges.forEach(badge -> result.add(new BadgeInfo(badge.getId(), badge.getName())));
        }
        return result;
    }
}
