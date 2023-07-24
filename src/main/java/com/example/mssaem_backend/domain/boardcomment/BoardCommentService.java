package com.example.mssaem_backend.domain.boardcomment;

import static com.example.mssaem_backend.global.common.Time.calculateTime;

import com.example.mssaem_backend.domain.badge.Badge;
import com.example.mssaem_backend.domain.badge.BadgeRepository;
import com.example.mssaem_backend.domain.boardcomment.dto.BoardCommentResponseDto.BoardCommentSimpleInfo;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BoardCommentService {

    private final BoardCommentRepository boardCommentRepository;
    private final BadgeRepository badgeRepository;

    public List<BoardCommentSimpleInfo> setBoardCommentSimpleInfo(
        List<BoardComment> boardComments) {
        List<BoardCommentSimpleInfo> boardCommentSimpleInfoList = new ArrayList<>();


        /*
        Boolean isAllowed = (viewer != null && viewer.getId().equals(member.getId()));
        for (BoardComment boardComment : boardComments) {
            boardCommentSimpleInfoList.add(
                new BoardCommentSimpleInfo(
                    boardComment.getContent(),
                    boardComment.getLikeCount(),
                    boardComment.getDepth(),
                    boardComment.getParentId(),
                    boardComment.getOrders(),
                    boardComment.isState(),
                    calculateTime(boardComment.getCreatedAt(), 3),
                    new MemberSimpleInfo(
                        boardComment.getMember().getId(),
                        boardComment.getMember().getNickName(),
                        boardComment.getMember().getMbti(),
                        badgeRepository.findBadgeByMemberAndStateTrue(boardComment.getMember())
                            .orElse(new Badge()).getName(),
                        boardComment.getMember().getProfileImageUrl()
                    )
                )
            );
        }*/
        return boardCommentSimpleInfoList;
    }

/*    public List<BoardCommentSimpleInfo> findBoardCommentListByBoardId(Member viewer, Long boardId) {
        //return boardCommentRepository.findAllByBoardId(boardId);
    }*/


}
