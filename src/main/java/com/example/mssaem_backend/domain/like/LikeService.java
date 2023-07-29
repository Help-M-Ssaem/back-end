package com.example.mssaem_backend.domain.like;

import com.example.mssaem_backend.domain.board.BoardRepository;
import com.example.mssaem_backend.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;

    private final BoardRepository boardRepository;

    @Transactional
    public Boolean updateBoardLike(Member member, Long boardId) {
        //해당 Board Like가 member, boardId 에 대해 존재하는지 확인
        if (likeRepository.existsLikeByMemberAndBoardId(member, boardId)) {
            //존재한다면 해당 Like 상태 변경
            Like like = likeRepository.findByMemberAndBoardId(member, boardId);
            like.updateBoardLike();
        } else {
            //존재하지 않는다면 새로운 Like 추가
            likeRepository.save(
                new Like(boardRepository.findByMemberAndIdAndStateIsTrue(member, boardId), member));
        }
        return likeRepository.findByMemberAndBoardId(member, boardId).nowBoardLikeState();
    }
}
