package com.example.mssaem_backend.domain.discussion;

import com.example.mssaem_backend.domain.badge.Badge;
import com.example.mssaem_backend.domain.badge.BadgeRepository;
import com.example.mssaem_backend.domain.discussion.dto.DiscussionResponseDto.DiscussionSimpleInfo;
import com.example.mssaem_backend.domain.discussioncomment.DiscussionCommentRepository;
import com.example.mssaem_backend.domain.discussionoption.DiscussionOption;
import com.example.mssaem_backend.domain.discussionoption.DiscussionOptionRepository;
import com.example.mssaem_backend.domain.discussionoption.dto.DiscussionOptionResponseDto.DiscussionOptionInfo;
import com.example.mssaem_backend.domain.discussionoption.dto.DiscussionOptionResponseDto.DiscussionOptionSelectedInfo;
import com.example.mssaem_backend.domain.discussionoptionselected.DiscussionOptionSelectedRepository;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.global.common.Time;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DiscussionService {

    private final DiscussionRepository discussionRepository;
    private final DiscussionOptionRepository discussionOptionRepository;
    private final DiscussionOptionSelectedRepository discussionOptionSelectedRepository;
    private final DiscussionCommentRepository discussionCommentRepository;
    private final BadgeRepository badgeRepository;

    // HOT 토론글 전체 조회
    public PageResponseDto<List<DiscussionSimpleInfo>> findHotDiscussionList(Member member,
        int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Discussion> discussions = discussionRepository.findDiscussionWithMoreThanTenParticipantsInLastThreeDaysAndStateTrue(
            LocalDateTime.now().minusDays(3)
            , pageRequest);

        return new PageResponseDto<>(
            discussions.getNumber(),
            discussions.getTotalPages(),
            setDiscussionSimpleInfo(
                member,
                discussions
                    .stream()
                    .collect(Collectors.toList()))
        );
    }

    // 최상위 제외한 HOT 토론글 2개만 조회
    public List<DiscussionSimpleInfo> findHotDiscussionListForHome(Member member) {
        PageRequest pageRequest = PageRequest.of(0, 3);
        List<Discussion> discussions = discussionRepository.findDiscussionWithMoreThanTenParticipantsInLastThreeDaysAndStateTrue(
                LocalDateTime.now().minusDays(3)
                , pageRequest)
            .stream()
            .collect(Collectors.toList());

        if (!discussions.isEmpty()) {
            discussions.remove(0);
        }

        return setDiscussionSimpleInfo(member, discussions);
    }

    // 토론글의 정보를 Dto에 매핑하는 메소드
    private List<DiscussionSimpleInfo> setDiscussionSimpleInfo(Member member,
        List<Discussion> discussions) {
        List<DiscussionSimpleInfo> discussionSimpleInfos = new ArrayList<>();

        int selectedOptionIdx = -1;
        List<DiscussionOption> discussionOptions;
        for (Discussion discussion : discussions) {
            discussionOptions = discussionOptionRepository
                .findDiscussionOptionByDiscussion(
                    discussion
                );

            if (member != null) {
                selectedOptionIdx = getSelectedOptionIdx(member, discussionOptions);
            }

            discussionSimpleInfos.add(
                new DiscussionSimpleInfo(
                    discussion.getId(),
                    discussion.getTitle(),
                    discussion.getContent(),
                    discussion.getParticipantCount(),
                    discussionCommentRepository.countWithStateTrueByDiscussion(discussion),
                    Time.calculateTime(discussion.getCreatedAt(), 3),
                    new MemberSimpleInfo(
                        discussion.getMember().getId(),
                        discussion.getMember().getNickName(),
                        discussion.getMember().getMbti(),
                        badgeRepository.findBadgeWithStateTrueByMember(
                                discussion.getMember())
                            .orElse(new Badge())
                            .getName(),
                        discussion.getMember().getProfileImageUrl()
                    ),
                    selectedOptionIdx != -1
                        ? setDiscussionOptionSelectedInfo(discussion.getParticipantCount(),
                        discussionOptions, selectedOptionIdx)
                        : setDiscussionOptionInfo(discussionOptions)
                )
            );
        }
        return discussionSimpleInfos;
    }

    // 로그인한 유저가 선택한 옵션의 idx 반환 (선택 안한 경우 -1)
    private int getSelectedOptionIdx(Member member, List<DiscussionOption> discussionOptions) {
        int selectedOptionIdx = -1;
        for (int i = 0; i < discussionOptions.size(); i++) {
            if (discussionOptionSelectedRepository.findDiscussionOptionSelectedWithStateByMemberAndDiscussionOption(
                member,
                discussionOptions.get(i)) != null) {
                selectedOptionIdx = i;
            }

            if (selectedOptionIdx != -1) {
                break;
            }
        }

        return selectedOptionIdx;
    }

    // 로그인한 유저가 옵션을 선택한 경우 고민글 옵션 정보 Dto에 저장
    private List<DiscussionOptionSelectedInfo> setDiscussionOptionSelectedInfo(Long participants,
        List<DiscussionOption> discussionOptions, int selectedOptionIdx) {

        List<DiscussionOptionSelectedInfo> discussionOptionSelectedInfos = new ArrayList<>();
        DiscussionOption discussionOption;
        String selectedPercent;
        for (int i = 0; i < discussionOptions.size(); i++) {
            discussionOption = discussionOptions.get(i);
            // 참여자 퍼센트 계산
            selectedPercent = String.format("%.2f",
                (double) discussionOption.getSelectCount() / (double) participants * 100.0);

            // 유저가 선택을 완료한 고민글 Dto 처리
            discussionOptionSelectedInfos.add(
                new DiscussionOptionSelectedInfo(
                    discussionOption.getId(),
                    discussionOption.getContent(),
                    discussionOption.getImgUrl(),
                    selectedPercent,
                    i == selectedOptionIdx)
            );
        }
        return discussionOptionSelectedInfos;
    }

    // 옵션을 선택하지 않은 경우 고민글 옵션 정보 Dto에 매핑
    private List<DiscussionOptionInfo> setDiscussionOptionInfo(
        List<DiscussionOption> discussionOptions) {
        List<DiscussionOptionInfo> discussionOptionInfos = new ArrayList<>();

        for (DiscussionOption discussionOption : discussionOptions) {
            discussionOptionInfos.add(
                new DiscussionOptionInfo(
                    discussionOption.getId(),
                    discussionOption.getContent(),
                    discussionOption.getImgUrl()
                )
            );
        }
        return discussionOptionInfos;
    }
}
