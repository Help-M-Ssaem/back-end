package com.example.mssaem_backend.domain.discussioncomment;

import static com.example.mssaem_backend.global.common.CheckWriter.isMatch;
import static com.example.mssaem_backend.global.common.Time.calculateTime;

import com.example.mssaem_backend.domain.discussioncomment.dto.DiscussionCommentResponseDto.DiscussionCommentSimpleInfo;
import com.example.mssaem_backend.domain.discussioncommentlike.DiscussionCommentLikeRepository;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DiscussionCommentService {

    private final DiscussionCommentRepository discussionCommentRepository;
    private final DiscussionCommentLikeRepository discussionCommentLikeRepository;

    public List<DiscussionCommentSimpleInfo> setDiscussionCommentSimpleInfo(List<DiscussionComment> discussionComments,
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
                            discussionComment.getMember(), discussionComment.getId())) //댓글 좋아요 눌렀는지 안눌렀는지 확인
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

        Page<DiscussionComment> result = discussionCommentRepository.findAllByDiscussionId(DiscussionId,
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
}
