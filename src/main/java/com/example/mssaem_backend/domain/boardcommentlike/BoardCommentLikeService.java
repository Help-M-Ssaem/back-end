package com.example.mssaem_backend.domain.boardcommentlike;

import com.example.mssaem_backend.domain.boardcomment.BoardCommentRepository;
import com.example.mssaem_backend.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BoardCommentLikeService {

    private final BoardCommentLikeRepository boardCommentLikeRepository;

    private final BoardCommentRepository boardCommentRepository;

    @Transactional
    public Boolean updateBoardCommentLike(Member member, Long boardId, Long commentId) {
        //해당 댓글 Like 가 member,BoardCommentId 에 대해 존재하는지 확인
        if (boardCommentLikeRepository.existsBoardCommentLikeByMemberAndBoardCommentId(
            member, commentId)) {
            boardCommentLikeRepository.findBoardCommentLikeByMemberAndBoardCommentId(member,
                commentId).updateBoardCommentLike();
        } else { //존재하지 않는다면 새로운 BoardCommentLike 생성
            boardCommentLikeRepository.save(new BoardCommentLike(
                boardCommentRepository.findByIdAndBoardIdAndStateIsTrue(commentId, boardId),
                member));
        } //True Or False 값 반환
        return boardCommentLikeRepository.findBoardCommentLikeByMemberAndBoardCommentId(member,
            commentId).nowBoardCommentLikeState();
    }

}
