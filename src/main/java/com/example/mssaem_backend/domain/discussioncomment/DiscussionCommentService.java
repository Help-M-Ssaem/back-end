package com.example.mssaem_backend.domain.discussioncomment;

import static com.example.mssaem_backend.global.common.CheckWriter.isMatch;
import static com.example.mssaem_backend.global.common.Time.calculateTime;

import com.example.mssaem_backend.domain.discussion.Discussion;
import com.example.mssaem_backend.domain.discussion.DiscussionRepository;
import com.example.mssaem_backend.domain.discussioncomment.dto.DiscussionCommentRequestDto.PostDiscussionCommentReq;
import com.example.mssaem_backend.domain.discussioncomment.dto.DiscussionCommentResponseDto.DiscussionCommentSimpleInfo;
import com.example.mssaem_backend.domain.discussioncommentlike.DiscussionCommentLikeRepository;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.domain.notification.NotificationService;
import com.example.mssaem_backend.domain.notification.TypeEnum;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.DiscussionErrorCode;
import com.example.mssaem_backend.global.config.exception.errorCode.MemberErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DiscussionCommentService {

    private final DiscussionCommentRepository discussionCommentRepository;
    private final DiscussionCommentLikeRepository discussionCommentLikeRepository;
    private final DiscussionRepository discussionRepository;
    private final NotificationService notificationService;

    public List<DiscussionCommentSimpleInfo> setDiscussionCommentSimpleInfo(
        List<DiscussionComment> discussionComments,
        Member viewer) {
        List<DiscussionCommentSimpleInfo> discussionCommentSimpleInfoList = new ArrayList<>();

        for (DiscussionComment discussionComment : discussionComments) {
            discussionCommentSimpleInfoList.add(
                DiscussionCommentSimpleInfo.builder()
                    .discussionComment(discussionComment)
                    .createdAt(calculateTime(discussionComment.getCreatedAt(), 3))
                    .isEditAllowed(isMatch(viewer,
                        discussionComment.getMember())) //해당 게시글을 보는 viewer 와 해당 댓글의 작성자와 같은지 확인
                    .isLiked(
                        discussionCommentLikeRepository.existsDiscussionCommentLikeByMemberAndStateIsTrueAndDiscussionCommentId(
                            discussionComment.getMember(),
                            discussionComment.getId())) //댓글 좋아요 눌렀는지 안눌렀는지 확인
                    .memberSimpleInfo(
                        new MemberSimpleInfo(
                            discussionComment.getMember().getId(),
                            discussionComment.getMember().getNickName(),
                            discussionComment.getMember().getDetailMbti(),
                            discussionComment.getMember().getBadgeName(),
                            discussionComment.getMember().getProfileImageUrl())
                    )
                    .build()
            );
        }
        return discussionCommentSimpleInfoList;
    }

    //댓글 조회
    public PageResponseDto<List<DiscussionCommentSimpleInfo>> findDiscussionCommentListByDiscussionId(
        Member viewer, Long DiscussionId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<DiscussionComment> result = discussionCommentRepository.findAllByDiscussionId(
            DiscussionId,
            pageable);

        return new PageResponseDto<>(
            result.getNumber(),
            result.getTotalPages(),
            setDiscussionCommentSimpleInfo(
                result
                    .stream()
                    .collect(Collectors.toList()), viewer)
        );
    }

    //댓글 작성
    @Transactional
    public Boolean createDiscussionComment(Member member, Long discussionId,
        PostDiscussionCommentReq postDiscussionCommentReq, Long commentId) {
        //해당 게시글이 없다면 예외처리
        Discussion discussion = discussionRepository.findById(discussionId)
            .orElseThrow(() -> new BaseException(DiscussionErrorCode.EMPTY_DISCUSSION));

        //만약 코멘트가 존재한다면 그 댓글에 대댓글
        if (discussionCommentRepository.existsDiscussionCommentById(commentId)) {
            DiscussionComment newDiscussionComment = discussionCommentRepository.save(
                new DiscussionComment(
                    postDiscussionCommentReq.getContent(),
                    member,
                    discussion,
                    commentId.intValue()));
            // 글을 쓴 멤버가 아닌 멤버가 댓글을 달 때만 알림 등록
            if (!discussion.getId().equals(member.getId())) {
                notificationService.createNotification(
                    newDiscussionComment.getId(),
                    postDiscussionCommentReq.getContent(),
                    TypeEnum.REPLY_OF_COMMENT,
                    discussion.getMember()
                );
            }
        } else { //존재하지 않다면 새로운 댓글
            DiscussionComment newDiscussionComment = discussionCommentRepository.save(
                new DiscussionComment(
                    postDiscussionCommentReq.getContent(),
                    member,
                    discussion,
                    0)
            );
            // 글을 쓴 멤버가 아닌 멤버가 댓글을 달 때만 알림 등록
            if (!discussion.getId().equals(member.getId())) {
                notificationService.createNotification(
                    newDiscussionComment.getId(),
                    postDiscussionCommentReq.getContent(),
                    TypeEnum.DISCUSSION_COMMENT,
                    discussion.getMember()
                );
            }
        }
        return true;
    }

    //댓글 삭제
    @Transactional
    public Boolean deleteDiscussionComment(Member member, Long discussionId, Long commentId) {
        DiscussionComment discussionComment = discussionCommentRepository.findByIdAndDiscussionIdAndStateIsTrue(
            commentId, discussionId);
        //현재 로그인한 멤버와 댓글 작성자가 같은지 확인
        if (isMatch(member, discussionComment.getMember())) {
            //같다면 삭제(삭제된 댓글입니다. 로 표시)
            discussionComment.deleteDiscussionComment();
            discussionCommentLikeRepository.deleteAllByDiscussionComment(discussionComment);

        } else {
            throw new BaseException(MemberErrorCode.INVALID_MEMBER);
        }
        return discussionComment.isState();
    }

    // 좋아요 수 10개 이상으로 베스트 댓글 3개 조회
    public List<DiscussionCommentSimpleInfo> findDiscussionCommentBestListByDiscussionId(
        Member viewer,
        Long discussionId) {
        PageRequest pageRequest = PageRequest.of(0, 3);

        List<DiscussionComment> discussionCommentList = discussionCommentLikeRepository.findDiscussionCommentsByDiscussionIdWithMoreThanTenDiscussionCommentLikeAndStateTrue(
            pageRequest, discussionId).stream().collect(Collectors.toList());

        return setDiscussionCommentSimpleInfo(discussionCommentList, viewer);
    }
}
