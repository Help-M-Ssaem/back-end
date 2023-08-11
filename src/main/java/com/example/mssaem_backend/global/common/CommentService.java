package com.example.mssaem_backend.global.common;

import static com.example.mssaem_backend.global.common.CheckWriter.isMatch;
import static com.example.mssaem_backend.global.common.Time.calculateTime;

import com.example.mssaem_backend.domain.board.BoardRepository;
import com.example.mssaem_backend.domain.boardcomment.BoardCommentRepository;
import com.example.mssaem_backend.domain.boardcommentlike.BoardCommentLikeRepository;
import com.example.mssaem_backend.domain.discussion.DiscussionRepository;
import com.example.mssaem_backend.domain.discussioncomment.DiscussionCommentRepository;
import com.example.mssaem_backend.domain.discussioncommentlike.DiscussionCommentLikeRepository;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.domain.notification.NotificationService;
import com.example.mssaem_backend.global.common.dto.CommentDto.GetCommentsByMemberRes;
import com.example.mssaem_backend.global.common.dto.CommentDto.GetCommentsRes;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final DiscussionCommentRepository discussionCommentRepository;
    private final DiscussionCommentLikeRepository discussionCommentLikeRepository;
    private final DiscussionRepository discussionRepository;

    private final BoardCommentRepository boardCommentRepository;
    private final BoardCommentLikeRepository boardCommentLikeRepository;
    private final BoardRepository boardRepository;

    private final NotificationService notificationService;

    //해당 댓글의 글 Id를 가져온다.
    private Long getPostId(Comment comment, String type) {
        return switch (type) {
            case "BOARD" -> comment.getBoard().getId();
            case "DISCUSSION" -> comment.getDiscussion().getId();
            default -> throw new IllegalArgumentException("올바르지 않은 댓글 타입: " + type);
        };
    }

    //해당 댓글에 현재 뷰어가 좋아요를 눌렀는지 확인한다.
    private boolean isCommentLiked(Member viewer, Comment comment, String type) {
        return switch (type) {
            case "BOARD" ->
                boardCommentLikeRepository.existsBoardCommentLikeByMemberAndStateIsTrueAndBoardCommentId(
                    viewer, comment.getId());
            case "DISCUSSION" ->
                discussionCommentLikeRepository.existsDiscussionCommentLikeByMemberAndStateIsTrueAndDiscussionCommentId(
                    viewer, comment.getId());
            default -> throw new IllegalArgumentException("올바르지 않은 댓글 타입: " + type);
        };
    }

    //CommentRes dto 세터
    public List<GetCommentsRes> setCommentsRes(Member viewer, List<Comment> comments, String type) {

        return comments.stream().map(comment -> {

            boolean isLiked = isCommentLiked(viewer, comment, type);

            return GetCommentsRes.builder().comment(comment).isLiked(isLiked)
                .createdAt(calculateTime(comment.getCreatedAt(), 3))
                .isEditAllowed(isMatch(viewer, comment.getMember())).memberSimpleInfo(
                    new MemberSimpleInfo(comment.getMember().getId(),
                        comment.getMember().getNickName(), comment.getMember().getDetailMbti(),
                        comment.getMember().getBadgeName(),
                        comment.getMember().getProfileImageUrl())).build();
        }).collect(Collectors.toList());
    }

    //CommentByMemberRes dto 세터
    public List<GetCommentsByMemberRes> setCommentsByMemberRes(Member viewer,
        List<Comment> comments, String type) {

        return comments.stream().map(comment -> {
            Long postId = getPostId(comment, type);
            boolean isLiked = isCommentLiked(viewer, comment, type);

            return GetCommentsByMemberRes.builder()
                .postId(postId)
                .comment(comment).isLiked(isLiked)
                .createdAt(calculateTime(comment.getCreatedAt(), 3))
                .isEditAllowed(isMatch(viewer, comment.getMember()))
                .memberSimpleInfo(
                    new MemberSimpleInfo(comment.getMember().getId(),
                        comment.getMember().getNickName(), comment.getMember().getDetailMbti(),
                        comment.getMember().getBadgeName(),
                        comment.getMember().getProfileImageUrl())
                ).build();
        }).collect(Collectors.toList());
    }

    //댓글 목록 조회
    public PageResponseDto<List<GetCommentsRes>> findCommentsByPostId(Member viewer, Long postId,
        int page, int size, String type) {
        Pageable pageable = PageRequest.of(page, size);

        Page<? extends Comment> comments = switch (type) {
            case "BOARD" -> boardCommentRepository.findAllByBoardId(postId, pageable);
            case "DISCUSSION" ->
                discussionCommentRepository.findAllByDiscussionId(postId, pageable);
            default -> throw new IllegalArgumentException("올바르지 않은 댓글 타입: " + type);
        };

        List<GetCommentsRes> commentResList = setCommentsRes(viewer,
            comments.stream().collect(Collectors.toList()), type);
        return new PageResponseDto<>(comments.getNumber(), comments.getTotalPages(),
            commentResList);
    }

    //베스트 댓글 3개 조회
    public List<GetCommentsRes> findBestCommentsByPostId(Member viewer, Long postId,
        String type) {
        PageRequest pageRequest = PageRequest.of(0, 3);

        Page<? extends Comment> comments = switch (type) {
            case "BOARD" -> boardCommentRepository.findAllByBoardId(postId, pageRequest);
            case "DISCUSSION" ->
                discussionCommentRepository.findAllByDiscussionId(postId, pageRequest);
            default -> throw new IllegalArgumentException("올바르지 않은 댓글 타입: " + type);
        };

        return setCommentsRes(viewer,
            comments.stream().collect(Collectors.toList()), type);
    }

    //멤버별 댓글 조회
    public PageResponseDto<List<GetCommentsByMemberRes>> findCommentsByMember(
        Long memberId, int page, int size, Member viewer, String type) {
        Pageable pageable = PageRequest.of(page, size);

        Page<? extends Comment> comments = switch (type) {
            case "BOARD" -> boardCommentRepository.findAllByMemberIdAndStateIsTrue(memberId,
                pageable);
            case "DISCUSSION" ->
                discussionCommentRepository.findAllByDiscussionId(memberId, pageable);
            default -> throw new IllegalArgumentException("올바르지 않은 댓글 타입: " + type);
        };

        List<GetCommentsByMemberRes> commentsByMemberResList = setCommentsByMemberRes(viewer,
            comments.stream().collect(Collectors.toList()), type);
        return new PageResponseDto<>(comments.getNumber(), comments.getTotalPages(), commentsByMemberResList);
    }

    //댓글 작성

    //댓글 삭제

}
