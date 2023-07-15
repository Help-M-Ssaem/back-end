package com.example.mssaem_backend.domain.board;

import com.example.mssaem_backend.domain.badge.BadgeRepository;
import com.example.mssaem_backend.domain.board.dto.BoardResponseDto.BoardSimpleInfo;
import com.example.mssaem_backend.domain.boardcomment.BoardCommentRepository;
import com.example.mssaem_backend.domain.boardimage.BoardImageRepository;
import com.example.mssaem_backend.domain.like.LikeRepository;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.global.common.Time;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final LikeRepository likeRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final BadgeRepository badgeRepository;
    private final BoardImageRepository boardImageRepository;

    public PageResponseDto<List<BoardSimpleInfo>> findHotBoardList(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return setBoardSimpleInfo(
            likeRepository.findBoardsWithMoreThanTenLikesInLastThreeDays(
                LocalDateTime.now().minusDays(3),
                pageRequest
            )
        );
    }

    private PageResponseDto<List<BoardSimpleInfo>> setBoardSimpleInfo(Page<Board> boards) {
        List<BoardSimpleInfo> boardSimpleInfos = new ArrayList<>();

        for (Board board : boards) {
            boardSimpleInfos.add(
                new BoardSimpleInfo(
                    board.getId(),
                    board.getTitle(),
                    board.getContent(),
                    boardImageRepository.findTopByBoardOrderById(board).getImageUrl(),
                    board.getMbti(),
                    board.getRecommendation(),
                    boardCommentRepository.countByBoardAndState(board, true),
                    Time.calculateTime(board.getCreatedAt(), 3),
                    new MemberSimpleInfo(
                        board.getMember().getId(),
                        board.getMember().getNickName(),
                        board.getMember().getMbti(),
                        badgeRepository.findBadgeByMemberAndState(board.getMember(), true)
                            .getName(),
                        board.getMember().getProfileImageUrl()
                    )
                )
            );
        }

        return new PageResponseDto<>(boards.getNumber(), boards.getTotalPages(), boardSimpleInfos);
    }

}
