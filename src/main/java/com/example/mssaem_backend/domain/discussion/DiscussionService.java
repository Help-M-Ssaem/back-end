package com.example.mssaem_backend.domain.discussion;

import static com.example.mssaem_backend.global.common.CheckWriter.match;

import com.example.mssaem_backend.domain.badge.BadgeRepository;
import com.example.mssaem_backend.domain.discussion.dto.DiscussionRequestDto.DiscussionReq;
import com.example.mssaem_backend.domain.discussion.dto.DiscussionResponseDto.DiscussionSimpleInfo;
import com.example.mssaem_backend.domain.discussioncomment.DiscussionCommentRepository;
import com.example.mssaem_backend.domain.discussionoption.DiscussionOption;
import com.example.mssaem_backend.domain.discussionoption.DiscussionOptionRepository;
import com.example.mssaem_backend.domain.discussionoption.DiscussionOptionService;
import com.example.mssaem_backend.domain.discussionoption.dto.DiscussionOptionResponseDto.DiscussionOptionInfo;
import com.example.mssaem_backend.domain.discussionoption.dto.DiscussionOptionResponseDto.DiscussionOptionSelectedInfo;
import com.example.mssaem_backend.domain.discussionoptionselected.DiscussionOptionSelectedRepository;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.global.common.Time;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.DiscussionErrorCode;
import com.example.mssaem_backend.global.s3.S3Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class DiscussionService {

    private final DiscussionRepository discussionRepository;
    private final DiscussionOptionService discussionOptionService;
    private final DiscussionOptionRepository discussionOptionRepository;
    private final DiscussionOptionSelectedRepository discussionOptionSelectedRepository;
    private final DiscussionCommentRepository discussionCommentRepository;
    private final BadgeRepository badgeRepository;
    private final S3Service s3Service;

    // HOT 토론글 더보기 조회
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
                    .collect(Collectors.toList()),
                3)
        );
    }

    // 홈 화면 - 최상위 제외한 HOT 토론글 2개만 조회
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

        return setDiscussionSimpleInfo(member, discussions, 1);
    }

    // 토론글의 정보를 Dto에 매핑하는 메소드
    private List<DiscussionSimpleInfo> setDiscussionSimpleInfo(Member member,
        List<Discussion> discussions, int dateType) {
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
                    discussionCommentRepository.countByDiscussionAndStateTrue(discussion),
                    Time.calculateTime(discussion.getCreatedAt(), dateType),
                    new MemberSimpleInfo(
                        discussion.getMember().getId(),
                        discussion.getMember().getNickName(),
                        discussion.getMember().getDetailMbti(),
                        badgeRepository.findNameMemberAndStateTrue(discussion.getMember())
                            .orElse(null),
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
            if (discussionOptionSelectedRepository.findDiscussionOptionSelectedByMemberAndDiscussionOptionAndStateTrue(
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

    //토른글 생성하기
    @Transactional
    public String createDiscussion(Member member, List<MultipartFile> multipartFiles,
        DiscussionReq postDiscussionReq) {
        //Discussion 생성
        Discussion discussion = Discussion.builder()
            .title(postDiscussionReq.getTitle())
            .content(postDiscussionReq.getContent())
            .member(member)
            .build();
        discussionRepository.save(discussion);

        //DiscussionOption 생성
        discussionOptionService.createOption(discussion, postDiscussionReq, multipartFiles);
        return "토론글 생성완료";
    }

    @Transactional
    public String modifyDiscussion(Member member, Long id, DiscussionReq patchDiscussionReq,
        List<MultipartFile> multipartFiles) {
        //수정 권한 확인
        Discussion discussion = discussionRepository.findById(id)
            .orElseThrow(() -> new BaseException(DiscussionErrorCode.EMPTY_DISCUSSION));
        match(member, discussion.getMember());

        //discussion 수정하기
        discussion.modifyDiscussion(
            patchDiscussionReq.getTitle(),
            patchDiscussionReq.getContent()
        );

        discussionOptionService.deleteOption(discussion);
        discussionOptionService.createOption(discussion, patchDiscussionReq, multipartFiles);

        return "토론글 수정완료";
    }

    @Transactional
    public String deleteDiscussion(Member member, Long id) {
        //삭제 권한 확인
        Discussion discussion = discussionRepository.findById(id)
            .orElseThrow(()-> new BaseException(DiscussionErrorCode.EMPTY_DISCUSSION));
        match(member, discussion.getMember());

        discussionOptionService.deleteOption(discussion);
        discussion.deleteDiscussion();

        return "토론글 삭제완료";
    }
}