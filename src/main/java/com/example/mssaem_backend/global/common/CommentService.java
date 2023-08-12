package com.example.mssaem_backend.global.common;

import static com.example.mssaem_backend.global.common.CheckWriter.isMatch;
import static com.example.mssaem_backend.global.common.Time.calculateTime;

import com.example.mssaem_backend.domain.board.Board;
import com.example.mssaem_backend.domain.board.BoardRepository;
import com.example.mssaem_backend.domain.boardcomment.BoardComment;
import com.example.mssaem_backend.domain.boardcomment.BoardCommentRepository;
import com.example.mssaem_backend.domain.boardcommentlike.BoardCommentLikeRepository;
import com.example.mssaem_backend.domain.discussion.Discussion;
import com.example.mssaem_backend.domain.discussion.DiscussionRepository;
import com.example.mssaem_backend.domain.discussioncomment.DiscussionComment;
import com.example.mssaem_backend.domain.discussioncomment.DiscussionCommentRepository;
import com.example.mssaem_backend.domain.discussioncommentlike.DiscussionCommentLikeRepository;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.domain.notification.NotificationService;
import com.example.mssaem_backend.domain.notification.TypeEnum;
import com.example.mssaem_backend.global.common.dto.CommentDto.GetCommentsByMemberRes;
import com.example.mssaem_backend.global.common.dto.CommentDto.GetCommentsRes;
import com.example.mssaem_backend.global.common.dto.CommentDto.PostCommentReq;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.BoardErrorCode;
import com.example.mssaem_backend.global.config.exception.errorCode.DiscussionErrorCode;
import com.example.mssaem_backend.global.config.exception.errorCode.MemberErrorCode;
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
public class CommentService {

    private final DiscussionCommentRepository discussionCommentRepository;
    private final DiscussionCommentLikeRepository discussionCommentLikeRepository;
    private final DiscussionRepository discussionRepository;

    private final BoardCommentRepository boardCommentRepository;
    private final BoardCommentLikeRepository boardCommentLikeRepository;
    private final BoardRepository boardRepository;

    private final NotificationService notificationService;

    //해당 댓글의 글 Id를 가져온다
    private Long getPostId(Comment comment, CommentTypeEnum commentType) {
        return switch (commentType) {
            case BOARD -> comment.getBoard().getId();
            case DISCUSSION -> comment.getDiscussion().getId();
        };
    }

    //해당 댓글에 현재 뷰어가 좋아요를 눌렀는지 확인
    private boolean isCommentLiked(Member viewer, Comment comment, CommentTypeEnum commentType) {
        return switch (commentType) {
            case BOARD ->
                boardCommentLikeRepository.existsBoardCommentLikeByMemberAndStateIsTrueAndBoardCommentId(
                    viewer, comment.getId());
            case DISCUSSION ->
                discussionCommentLikeRepository.existsDiscussionCommentLikeByMemberAndStateIsTrueAndDiscussionCommentId(
                    viewer, comment.getId());
        };
    }

    //CommentRes dto 세터
    private List<GetCommentsRes> setCommentsRes(Member viewer, List<Comment> comments,
        CommentTypeEnum commentType) {

        return comments.stream().map(comment -> {

            boolean isLiked = isCommentLiked(viewer, comment, commentType);

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
    private List<GetCommentsByMemberRes> setCommentsByMemberRes(Member viewer,
        List<Comment> comments, CommentTypeEnum commentType) {

        return comments.stream().map(comment -> {
            Long postId = getPostId(comment, commentType);
            boolean isLiked = isCommentLiked(viewer, comment, commentType);

            return GetCommentsByMemberRes.builder().postId(postId).comment(comment).isLiked(isLiked)
                .createdAt(calculateTime(comment.getCreatedAt(), 3))
                .isEditAllowed(isMatch(viewer, comment.getMember())).memberSimpleInfo(
                    new MemberSimpleInfo(comment.getMember().getId(),
                        comment.getMember().getNickName(), comment.getMember().getDetailMbti(),
                        comment.getMember().getBadgeName(),
                        comment.getMember().getProfileImageUrl())).build();
        }).collect(Collectors.toList());
    }

    //댓글 목록 조회
    public PageResponseDto<List<GetCommentsRes>> findCommentsByPostId(Member viewer, Long postId,
        int page, int size, CommentTypeEnum commentType) {
        Pageable pageable = PageRequest.of(page, size);

        Page<? extends Comment> comments = switch (commentType) {
            case BOARD -> boardCommentRepository.findAllByBoardId(postId, pageable);
            case DISCUSSION -> discussionCommentRepository.findAllByDiscussionId(postId, pageable);
        };

        List<GetCommentsRes> commentResList = setCommentsRes(viewer,
            comments.stream().collect(Collectors.toList()), commentType);
        return new PageResponseDto<>(comments.getNumber(), comments.getTotalPages(),
            commentResList);
    }

    //베스트 댓글 3개 조회
    public List<GetCommentsRes> findBestCommentsByPostId(Member viewer, Long postId,
        CommentTypeEnum commentType) {
        PageRequest pageRequest = PageRequest.of(0, 3);

        Page<? extends Comment> comments = switch (commentType) {
            case BOARD ->
                boardCommentLikeRepository.findBoardCommentsByBoardIdWithMoreThanTenBoardCommentLikeAndStateTrue(
                    pageRequest, postId);
            case DISCUSSION ->
                discussionCommentLikeRepository.findDiscussionCommentsByDiscussionIdWithMoreThanTenDiscussionCommentLikeAndStateTrue(
                    pageRequest, postId);
        };

        return setCommentsRes(viewer, comments.stream().collect(Collectors.toList()), commentType);
    }

    //멤버별 댓글 조회
    public PageResponseDto<List<GetCommentsByMemberRes>> findCommentsByMember(Long memberId,
        int page, int size, Member viewer, CommentTypeEnum commentType) {
        Pageable pageable = PageRequest.of(page, size);

        Page<? extends Comment> comments = switch (commentType) {
            case BOARD ->
                boardCommentRepository.findAllByMemberIdAndStateIsTrue(memberId, pageable);
            case DISCUSSION ->
                discussionCommentRepository.findAllByDiscussionId(memberId, pageable);
        };

        List<GetCommentsByMemberRes> commentsByMemberResList = setCommentsByMemberRes(viewer,
            comments.stream().collect(Collectors.toList()), commentType);
        return new PageResponseDto<>(comments.getNumber(), comments.getTotalPages(),
            commentsByMemberResList);
    }

    // 댓글 작성
    @Transactional
    public String createComment(Member member, Long objectId, PostCommentReq postCommentReq,
        Long commentId, CommentTypeEnum commentType, boolean isReply) {

        Comment parentComment = null;
        String content = postCommentReq.getContent();

        switch (commentType) {
            case BOARD -> {
                Board board = boardRepository.findById(objectId)
                    .orElseThrow(() -> new BaseException(BoardErrorCode.EMPTY_BOARD));

                //BoardComment 엔티티 생성
                BoardComment boardComment = new BoardComment(content, member, board);
                //만약 대댓글이면 부모 설정
                if (isReply) {
                    boardComment.setParentComment(commentId);
                    //알림에 사용할 부모 댓글 가져오기
                    parentComment = boardCommentRepository.findByIdAndBoardIdAndStateIsTrue(
                        commentId,
                        objectId);
                }
                boardCommentRepository.save(boardComment);

                //board 알림
                //일반 댓글인 경우
                if (!isMatch(board.getMember(), member)) {
                    notificationService.createNotification(objectId, content,
                        TypeEnum.BOARD_COMMENT, board.getMember());
                }
                //대댓글인 경우
                if (isReply && !isMatch(parentComment.getMember(), member)) {
                    notificationService.createNotification(objectId, content,
                        TypeEnum.BOARD_REPLY_OF_COMMENT, board.getMember());
                }
            }
            case DISCUSSION -> {
                Discussion discussion = discussionRepository.findById(objectId)
                    .orElseThrow(() -> new BaseException(DiscussionErrorCode.EMPTY_DISCUSSION));

                //DiscussionComment 엔티티 생성
                DiscussionComment discussionComment = new DiscussionComment(content, member,
                    discussion);
                //대댓글이면 부모 설정
                if (isReply) {
                    discussionComment.setParentComment(commentId);
                    //알림을 위해 부모 댓글 가져오기
                    parentComment = discussionCommentRepository.findByIdAndDiscussionIdAndStateIsTrue(
                        commentId, objectId);
                }
                discussionCommentRepository.save(discussionComment);

                //discussion 알림
                //일반 댓글인 경우
                if (!isMatch(discussion.getMember(), member)) {
                    notificationService.createNotification(objectId, content,
                        TypeEnum.DISCUSSION_COMMENT, discussion.getMember());
                }
                //대댓글인 경우
                if (isReply && !isMatch(parentComment.getMember(), member)) {
                    notificationService.createNotification(objectId, content,
                        TypeEnum.DISCUSSION_REPLY_OF_COMMENT, discussion.getMember());
                }
            }
        }
        return "댓글 작성에 성공";
    }

    //댓글 삭제
    @Transactional
    public String deleteComment(Member member, Long objectId, Long commentId,
        CommentTypeEnum commentType) {
        Comment comment;

        if (commentType == CommentTypeEnum.BOARD) {
            comment = boardCommentRepository.findByIdAndBoardIdAndStateIsTrue(commentId, objectId);
        } else {
            comment = discussionCommentRepository.findByIdAndDiscussionIdAndStateIsTrue(commentId,
                objectId);
        }

        if (isMatch(member, comment.getMember())) {
            // 같다면 삭제(삭제된 댓글입니다. 로 표시)
            comment.deleteComment();

            if (comment instanceof BoardComment) {
                // 댓글 삭제시 해당 댓글에 달린 좋아요 삭제
                boardCommentLikeRepository.deleteAllByBoardComment((BoardComment) comment);
            } else {
                discussionCommentLikeRepository.deleteAllByDiscussionComment(
                    (DiscussionComment) comment);
            }
        } else {
            throw new BaseException(MemberErrorCode.INVALID_MEMBER);
        }

        return "댓글 삭제에 성공";
    }

    //글 삭제 시 해당 글 댓글 전체 삭제
    @Transactional
    public void deleteAllComments(Long postId, CommentTypeEnum commentType) {

        //Board 처리
        if (commentType == CommentTypeEnum.BOARD) {
            Board board = boardRepository.findByIdAndStateIsTrue(postId)
                .orElseThrow(() -> new BaseException(BoardErrorCode.EMPTY_BOARD));

            List<BoardComment> boardComments = boardCommentRepository.findAllByBoardId(
                board.getId());

            for (BoardComment boardComment : boardComments) {
                boardCommentLikeRepository.deleteAllByBoardComment(boardComment);
            }

            boardCommentRepository.deleteAllByBoard(board);

        } else { //discussion 처리
            Discussion discussion = discussionRepository.findByIdAndStateIsTrue(postId)
                .orElse(null);

            if (discussion == null) {
                throw new BaseException(DiscussionErrorCode.EMPTY_DISCUSSION);
            }

            List<DiscussionComment> discussionComments = discussionCommentRepository.findAllByDiscussion(
                discussion);

            for (DiscussionComment discussionComment : discussionComments) {
                discussionCommentLikeRepository.deleteAllByDiscussionComment(discussionComment);
            }

            discussionCommentRepository.deleteAllByDiscussion(discussion);
        }
    }
}