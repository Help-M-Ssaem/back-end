package com.example.mssaem_backend.domain.discussion;

import static com.example.mssaem_backend.global.common.CheckWriter.isMatch;
import static com.example.mssaem_backend.global.common.CheckWriter.match;

import com.example.mssaem_backend.domain.badge.BadgeRepository;
import com.example.mssaem_backend.domain.discussion.dto.DiscussionRequestDto.DiscussionReq;
import com.example.mssaem_backend.domain.discussion.dto.DiscussionResponseDto.DiscussionDetailInfo;
import com.example.mssaem_backend.domain.discussion.dto.DiscussionResponseDto.DiscussionHistory;
import com.example.mssaem_backend.domain.discussion.dto.DiscussionResponseDto.DiscussionSimpleInfo;
import com.example.mssaem_backend.domain.discussioncomment.DiscussionCommentRepository;
import com.example.mssaem_backend.domain.discussionoption.DiscussionOption;
import com.example.mssaem_backend.domain.discussionoption.DiscussionOptionRepository;
import com.example.mssaem_backend.domain.discussionoption.DiscussionOptionService;
import com.example.mssaem_backend.domain.discussionoption.dto.DiscussionOptionResponseDto.DiscussionOptionInfo;
import com.example.mssaem_backend.domain.discussionoption.dto.DiscussionOptionResponseDto.DiscussionOptionLoginInfo;
import com.example.mssaem_backend.domain.discussionoptionselected.DiscussionOptionSelected;
import com.example.mssaem_backend.domain.discussionoptionselected.DiscussionOptionSelectedRepository;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.MemberRepository;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.domain.notification.NotificationService;
import com.example.mssaem_backend.domain.notification.TypeEnum;
import com.example.mssaem_backend.domain.search.dto.SearchRequestDto.SearchReq;
import com.example.mssaem_backend.global.common.Time;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.DiscussionErrorCode;
import com.example.mssaem_backend.global.config.exception.errorCode.MemberErrorCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final NotificationService notificationService;
    private final MemberRepository memberRepository;


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
    public List<DiscussionSimpleInfo> setDiscussionSimpleInfo(Member member,
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
                        ? setDiscussionOptionLoginInfo(discussion.getParticipantCount(),
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

    // 로그인한 유저가 옵션을 선택한 경우 고민글 옵션 정보 Dto에 매핑
    private List<DiscussionOptionLoginInfo> setDiscussionOptionLoginInfo(Long participants,
        List<DiscussionOption> discussionOptions, int selectedOptionIdx) {

        List<DiscussionOptionLoginInfo> DiscussionOptionLoginInfos = new ArrayList<>();
        DiscussionOption discussionOption;
        String selectedPercent;
        for (int i = 0; i < discussionOptions.size(); i++) {
            discussionOption = discussionOptions.get(i);
            // 참여자 퍼센트 계산
            selectedPercent = String.format("%.2f",
                (double) discussionOption.getSelectCount() / (double) participants * 100.0);

            // 유저가 선택을 완료한 고민글 Dto 처리
            DiscussionOptionLoginInfos.add(
                new DiscussionOptionLoginInfo(
                    discussionOption.getId(),
                    discussionOption.getContent(),
                    discussionOption.getImgUrl(),
                    selectedPercent,
                    i == selectedOptionIdx)
            );
        }
        return DiscussionOptionLoginInfos;
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

    public PageResponseDto<List<DiscussionSimpleInfo>> findDiscussionListByKeyword(Member member,
        SearchReq searchReq, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Discussion> discussions = discussionRepository.searchByType(searchReq.getType(),
            searchReq.getKeyword(), pageRequest);

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

    //토론글 수정하기
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

    //토론글 삭제하기
    @Transactional
    public String deleteDiscussion(Member member, Long id) {
        //삭제 권한 확인
        Discussion discussion = discussionRepository.findById(id)
            .orElseThrow(() -> new BaseException(DiscussionErrorCode.EMPTY_DISCUSSION));
        match(member, discussion.getMember());

        discussionOptionService.deleteOption(discussion);
        discussion.deleteDiscussion();

        return "토론글 삭제완료";
    }

    public DiscussionHistory getDiscussionHistory(Member member) {
        return new DiscussionHistory(
            discussionRepository.countAllByStateIsTrueAndMember(member),
            discussionCommentRepository.countAllByStateIsTrueAndMember(member),
            discussionRepository.sumParticipantCountByMember(member)
        );
    }

    //토론글 참여하기
    @Transactional
    public List<DiscussionOptionLoginInfo> participateDiscussion(Member member,
        Long discussionId, Long discussionOptionId) {
        Discussion discussion = discussionRepository.findById(discussionId)
            .orElseThrow(() -> new BaseException(DiscussionErrorCode.EMPTY_DISCUSSION));
        List<DiscussionOption> discussionOptions = discussionOptionRepository.findDiscussionOptionByDiscussion(
            discussion);

        //선택할 option 가져오기
        int selectIdx = 0;
        for (DiscussionOption discussionOption : discussionOptions) {
            if (discussionOption.getId().equals(discussionOptionId)) {
                break;
            }
            selectIdx++;
        }

        DiscussionOption discussionOption = discussionOptions.get(selectIdx);

        //첫 참여라면 -1 아니면 이전 선택 option idx반환
        int checkSelectedIdx = getSelectedOptionIdx(member, discussionOptions);

        //첫 참여라면
        if (checkSelectedIdx == -1) {
            DiscussionOptionSelected discussionOptionSelected = DiscussionOptionSelected.builder()
                .discussionOption(discussionOption)
                .member(member)
                .build();

            //discussion의 count수 증가
            discussion.increaseCount();
            //discussion의 참여자수가 10명 이상일 경우 HOT 토론이 됨
            if (discussion.getParticipantCount() == 10) {
                notificationService.createNotification(
                    discussionId,
                    discussion.getTitle(),
                    TypeEnum.HOT_DISCUSSION,
                    discussion.getMember()
                );
            }
            discussionOptionSelectedRepository.save(discussionOptionSelected);
        }

        //첫 참여가 아니라면 (선택한 옵션이 있다면)
        else {
            //이전에 선택했던 옵션과 optionSelected 불러오기
            DiscussionOption beforeDiscussionOption = discussionOptions.get(checkSelectedIdx);
            List<DiscussionOptionSelected> discussionOptionSelects = discussionOptionSelectedRepository.findAllByMemberAndDiscussionOrderByOptionIdAsc(
                member, discussion);

            //현재 선택한 option에 대해 optionSeleted가 존재하는지 확인
            //&& 직전 선택한 beforeOptionSelected idx값도 확인
            int beforeSelectedIdx = 0;
            boolean isOptionSelectExsist = false;
            for (int i = 0; i < discussionOptionSelects.size(); i++) {
                Long id = discussionOptionSelects.get(i).getDiscussionOption().getId();
                String content = discussionOptionSelects.get(i).getDiscussionOption().getContent();
                System.out.println(content);
                if (id.equals(discussionOptionId)) {
                    isOptionSelectExsist = true;
                }
                if (id.equals(beforeDiscussionOption.getId())) {
                    beforeSelectedIdx = i;
                }
            }

            // 이전에 선택했었던 option으로 선택하는 경우
            if (isOptionSelectExsist) {
                discussionOptionSelects.get(selectIdx).changeSelected();
            }
            //이전에 선택하지 않았던 option으로 선택하는 경우
            else {
                DiscussionOptionSelected newDiscussionOptionSelected = DiscussionOptionSelected.builder()
                    .discussionOption(discussionOption)
                    .member(member)
                    .build();

                discussionOptionSelectedRepository.save(newDiscussionOptionSelected);
            }

            //이전에 선택한 option과 optionSelected count 감소
            beforeDiscussionOption.decreaseCount();
            discussionOptionSelects.get(beforeSelectedIdx).changeUnselected();
        }

        //option count수 증가
        discussionOption.increaseCount();

        //해당 토론의 참여율 계산해서 반환
        return setDiscussionOptionLoginInfo(discussion.getParticipantCount(), discussionOptions,
            selectIdx);
    }

    //토론글 전체 조회하기
    public PageResponseDto<List<DiscussionSimpleInfo>> findDiscussions(Member member, int page,
        int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Discussion> discussions = discussionRepository.findByStateTrueOrderByCreatedAtDesc(
            pageRequest);
        List<Discussion> discussionList = discussions.stream().toList();
        return new PageResponseDto<>(discussions.getNumber(), discussions.getTotalPages(),
            setDiscussionSimpleInfo(member, discussionList, 3));
    }

    //토론글 상세 조회
    public DiscussionDetailInfo findDiscussion(Member viewer, Long id) {
        Discussion discussion = discussionRepository.findById(id)
            .orElseThrow(() -> new BaseException(DiscussionErrorCode.EMPTY_DISCUSSION));

        //수정,삭제 권한 확인
        Boolean isEditAllowed = isMatch(viewer, discussion.getMember());

        List<Discussion> discussions = new ArrayList<>();
        discussions.add(discussion);
        return new DiscussionDetailInfo(setDiscussionSimpleInfo(viewer, discussions, 2).get(0),
            isEditAllowed);
    }

    //특정 멤버별 올린 토론글 조회
    public PageResponseDto<List<DiscussionSimpleInfo>> findDiscussionsByMemberId(Member member,
        Long memberId,
        int page,
        int size) {
        Pageable pageable = PageRequest.of(page, size);
        //memberId가 있으면 타인의 프로필
        Member profileMember;
        if (memberId != null) {
            profileMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(MemberErrorCode.EMPTY_MEMBER));
        } else {
            profileMember = member;
        }

        Page<Discussion> discussions = discussionRepository.findAllByMemberAndStateIsTrueOrderByCreatedAtDesc(
            profileMember, pageable);
        return new PageResponseDto<>(
            discussions.getNumber(),
            discussions.getTotalPages(),
            setDiscussionSimpleInfo(
                profileMember,
                discussions
                    .stream()
                    .collect(Collectors.toList()), 3)
        );
    }
}