package com.example.mssaem_backend.domain.discussioncommentlike;

import com.example.mssaem_backend.domain.discussioncomment.DiscussionCommentRepository;
import com.example.mssaem_backend.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DiscussionCommentLikeService {

    private final DiscussionCommentLikeRepository discussionCommentLikeRepository;
    private final DiscussionCommentRepository discussionCommentRepository;

    @Transactional
    public Boolean updateDiscussionCommentLike(Member member, Long discussionId, Long commentId) {
        //해당 댓글 Like 가 member, DiscussionCommentId 에 대해 존재하는지 확인
        if (discussionCommentLikeRepository.existsDiscussionCommentLikeByMemberAndStateIsTrueAndDiscussionCommentId(
            member, commentId)) {
            discussionCommentLikeRepository.findDiscussionCommentLikeByMemberAndDiscussionCommentId(member,
                commentId).updateDiscussionCommentLike();
        } else { //존재하지 않는다면 새로운 DiscussionCommentLike 생성
            discussionCommentLikeRepository.save(new DiscussionCommentLike(
                discussionCommentRepository.findByIdAndDiscussionIdAndStateIsTrue(commentId, discussionId),
                member));
        } //True Or False 값 반환
        return discussionCommentLikeRepository.findDiscussionCommentLikeByMemberAndDiscussionCommentId(member,
            commentId).nowDiscussionCommentLikeState();
    }
}
