package com.example.mssaem_backend.domain.like;

import com.example.mssaem_backend.domain.board.Board;
import com.example.mssaem_backend.domain.board.BoardRepository;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.notification.NotificationService;
import com.example.mssaem_backend.domain.notification.TypeEnum;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.BoardErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;

    private final BoardRepository boardRepository;
    private final NotificationService notificationService;

    @Transactional
    public Boolean updateBoardLike(Member member, Long boardId) {
        //boardId에 대한 유효성 검사
        boardRepository.findById(boardId)
            .orElseThrow(() -> new BaseException(BoardErrorCode.EMPTY_BOARD));
        //해당 Board Like가 member, boardId 에 대해 존재하는지 확인
        if (likeRepository.existsLikeByMemberAndBoardId(member, boardId)) {
            //존재한다면 해당 Like 상태 변경
            likeRepository.findByMemberAndBoardId(member, boardId).updateBoardLike();
            //해당 게시물의 like 수가 10개 이상이면 HOT 게시글이 됨
            Board board = boardRepository.findByIdAndStateIsTrue(boardId)
                .orElseThrow(() -> new BaseException(BoardErrorCode.EMPTY_BOARD));
            if (board.getLikeCount() == 10) {
                // 해당 게시물의 like 수가 10개 이상이면 HOT 게시글이 됨
                notificationService.createNotification(
                    boardId,
                    board.getTitle(),
                    TypeEnum.HOT_BOARD,
                    board.getMember()
                );
            }
        } else {
            //존재하지 않는다면 새로운 Like 추가
            Board board = boardRepository.findByIdAndStateIsTrue(boardId)
                .orElseThrow(() -> new BaseException(BoardErrorCode.EMPTY_BOARD));
            likeRepository.save(new Like(board, member));
            if (board.getLikeCount() == 10) {
                // 해당 게시물의 like 수가 10개 이상이면 HOT 게시글이 됨
                notificationService.createNotification(
                    boardId,
                    board.getTitle(),
                    TypeEnum.HOT_BOARD,
                    board.getMember()
                );
            }
        }
        return likeRepository.findByMemberAndBoardId(member, boardId).nowBoardLikeState();
    }
}
