package com.example.mssaem_backend.domain.boardcomment;

import static com.example.mssaem_backend.global.common.CheckWriter.isMatch;
import static com.example.mssaem_backend.global.common.Time.calculateTime;

import com.example.mssaem_backend.domain.board.Board;
import com.example.mssaem_backend.domain.board.BoardRepository;
import com.example.mssaem_backend.domain.boardcomment.dto.BoardCommentRequestDto.PostBoardCommentReq;
import com.example.mssaem_backend.domain.boardcomment.dto.BoardCommentResponseDto.BoardCommentSimpleInfo;
import com.example.mssaem_backend.domain.boardcomment.dto.BoardCommentResponseDto.BoardCommentSimpleInfoByMember;
import com.example.mssaem_backend.domain.boardcomment.dto.BoardCommentResponseDto.PostBoardCommentRes;
import com.example.mssaem_backend.domain.boardcommentlike.BoardCommentLikeRepository;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.domain.notification.NotificationService;
import com.example.mssaem_backend.domain.notification.TypeEnum;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.BoardErrorCode;
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
public class BoardCommentService {

    private final BoardCommentRepository boardCommentRepository;
    private final BoardCommentLikeRepository boardCommentLikeRepository;
    private final BoardRepository boardRepository;
    private final NotificationService notificationService;

    public List<BoardCommentSimpleInfo> setBoardCommentSimpleInfo(List<BoardComment> boardComments,
        Member viewer) {
        List<BoardCommentSimpleInfo> boardCommentSimpleInfoList = new ArrayList<>();

        for (BoardComment boardComment : boardComments) {
            boardCommentSimpleInfoList.add(
                BoardCommentSimpleInfo.builder()
                    .boardComment(boardComment)
                    .createdAt(calculateTime(boardComment.getCreatedAt(), 3))
                    .isAllowed(isMatch(viewer,
                        boardComment.getMember())) //해당 댓글을 보는 viewer 와 해당 댓글의 작성자와 같은지 확인
                    .isLiked(
                        boardCommentLikeRepository.existsBoardCommentLikeByMemberAndStateIsTrueAndBoardCommentId(
                            boardComment.getMember(), boardComment.getId())) //댓글 좋아요 눌렀는지 안눌렀는지 확인
                    .memberSimpleInfo(
                        new MemberSimpleInfo(
                            boardComment.getMember().getId(),
                            boardComment.getMember().getNickName(),
                            boardComment.getMember().getDetailMbti(),
                            boardComment.getMember().getBadgeName(),
                            boardComment.getMember().getProfileImageUrl())
                    )
                    .build()
            );
        }
        return boardCommentSimpleInfoList;
    }

    public PageResponseDto<List<BoardCommentSimpleInfo>> findBoardCommentListByBoardId(
        Member viewer, Long boardId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<BoardComment> result = boardCommentRepository.findAllByBoardId(boardId,
            pageable);

        return new PageResponseDto<>(
            result.getNumber(),
            result.getTotalPages(),
            setBoardCommentSimpleInfo(
                result
                    .stream()
                    .collect(Collectors.toList()), viewer)
        );
    }

    //댓글 작성
    @Transactional
    public PostBoardCommentRes createBoardComment(Member member, Long boardId,
        PostBoardCommentReq postBoardCommentReq, Long commentId) {
        //해당 게시글이 없다면 예외처리
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new BaseException(BoardErrorCode.EMPTY_BOARD));

        //만약 코멘트가 존재한다면 그 댓글에 대댓글
        if (boardCommentRepository.existsBoardCommentById(commentId)) {
            BoardComment newBoardComment = boardCommentRepository.save(
                new BoardComment(
                    postBoardCommentReq.getContent(),
                    member,
                    board,
                    commentId.intValue()));
            // 글을 쓴 멤버가 아닌 멤버가 댓글을 달 때만 알림 등록
            if (!board.getId().equals(member.getId())) {
                notificationService.createNotification(
                    newBoardComment.getId(),
                    postBoardCommentReq.getContent(),
                    TypeEnum.REPLY_OF_COMMENT,
                    board.getMember()
                );
            }
        } else { //존재하지 않다면 새로운 댓글
            BoardComment newBoardComment = boardCommentRepository.save(
                new BoardComment(
                    postBoardCommentReq.getContent(),
                    member,
                    board,
                    0)
            );
            // 글을 쓴 멤버가 아닌 멤버가 댓글을 달 때만 알림 등록
            if (!board.getId().equals(member.getId())) {
                notificationService.createNotification(
                    newBoardComment.getId(),
                    postBoardCommentReq.getContent(),
                    TypeEnum.BOARD_COMMENT,
                    board.getMember()
                );
            }
        }
        //댓글 작성 시 parentId 와 content 반환
        return PostBoardCommentRes.builder()
            .parentId(
                boardCommentRepository.existsBoardCommentById(commentId) ? commentId.intValue() : 0)
            .content(postBoardCommentReq.getContent())
            .build();
    }

    //댓글 삭제
    @Transactional
    public Boolean deleteBoardComment(Member member, Long boardId, Long commentId) {
        BoardComment boardComment = boardCommentRepository.findByIdAndBoardIdAndStateIsTrue(
            commentId, boardId);
        //현재 로그인한 멤버와 댓글 작성자가 같은지 확인
        if (isMatch(member, boardComment.getMember())) {
            //같다면 삭제(삭제된 댓글입니다. 로 표시)
            boardComment.deleteBoardComment();
        } else {
            throw new BaseException(MemberErrorCode.INVALID_MEMBER);
        }
        return boardComment.isState();
    }

    // 좋아요 수 10개 이상으로 베스트 댓글 3개 조회
    public List<BoardCommentSimpleInfo> findBoardCommentBestListByBoardId(Member viewer,
        Long boardId) {
        PageRequest pageRequest = PageRequest.of(0, 3);

        List<BoardComment> boardCommentList = boardCommentLikeRepository.findBoardCommentsByBoardIdWithMoreThanTenBoardCommentLikeAndStateTrue(
            pageRequest, boardId).stream().collect(Collectors.toList());

        return setBoardCommentSimpleInfo(boardCommentList, viewer);
    }

    // 특정 멤버별 댓글 조회를 위한 매핑
    public List<BoardCommentSimpleInfoByMember> setBoardCommentSimpleInfoByBoard(
        List<BoardComment> boardComments, Member viewer) {
        List<BoardCommentSimpleInfoByMember> boardCommentSimpleInfoList = new ArrayList<>();

        for (BoardComment boardComment : boardComments) {
            boardCommentSimpleInfoList.add(
                BoardCommentSimpleInfoByMember.builder()
                    .boardId(boardComment.getBoard().getId())
                    .boardComment(boardComment)
                    .createdAt(calculateTime(boardComment.getCreatedAt(), 3))
                    .isAllowed(isMatch(viewer,
                        boardComment.getMember())) //해당 댓글 보는 viewer 와 해당 댓글의 작성자와 같은지 확인
                    .isLiked(
                        boardCommentLikeRepository.existsBoardCommentLikeByMemberAndStateIsTrueAndBoardCommentId(
                            boardComment.getMember(), boardComment.getId())) //댓글 좋아요 눌렀는지 안눌렀는지 확인
                    .memberSimpleInfo(
                        new MemberSimpleInfo(
                            boardComment.getMember().getId(),
                            boardComment.getMember().getNickName(),
                            boardComment.getMember().getDetailMbti(),
                            boardComment.getMember().getBadgeName(),
                            boardComment.getMember().getProfileImageUrl())
                    )
                    .build()
            );
        }
        return boardCommentSimpleInfoList;
    }

    //특정 멤버별 댓글 조회
    public PageResponseDto<List<BoardCommentSimpleInfoByMember>> findBoardCommentListByMemberId(
        Long memberId, int page, int size, Member viewer) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BoardComment> result = boardCommentRepository.findAllByMemberIdAndStateIsTrue(memberId,
            pageable);

        return new PageResponseDto<>(
            result.getNumber(),
            result.getTotalPages(),
            setBoardCommentSimpleInfoByBoard(
                result
                    .stream()
                    .collect(Collectors.toList()), viewer)
        );
    }

    //게시글 삭제 시 해당 게시글 댓글 완전 삭제
    @Transactional
    public Boolean deleteAllBoardComment(Board board) {
        List<BoardComment> comments = boardCommentRepository.findAllByBoardId(board.getId());
        //모든 댓글에 대한 댓글 좋아요 먼저 삭제
        for (BoardComment comment : comments) {
            boardCommentLikeRepository.deleteAllByBoardComment(comment);
        }
        // 게시글에 대한 모든 댓글 삭제
        boardCommentRepository.deleteAllByBoard(board);
        return true;
    }

}
