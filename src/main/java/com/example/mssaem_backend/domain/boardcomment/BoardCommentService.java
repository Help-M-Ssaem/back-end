package com.example.mssaem_backend.domain.boardcomment;

import static com.example.mssaem_backend.global.common.Time.calculateTime;

import com.example.mssaem_backend.domain.badge.BadgeRepository;
import com.example.mssaem_backend.domain.boardcomment.dto.BoardCommentResponseDto.BoardCommentSimpleInfo;
import com.example.mssaem_backend.domain.boardcommentlike.BoardCommentLikeRepository;
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
public class BoardCommentService {

    private final BoardCommentRepository boardCommentRepository;
    private final BadgeRepository badgeRepository;
    private final BoardCommentLikeRepository boardCommentLikeRepository;

    public List<BoardCommentSimpleInfo> setBoardCommentSimpleInfo(List<BoardComment> boardComments,
        Member viewer) {
        List<BoardCommentSimpleInfo> boardCommentSimpleInfoList = new ArrayList<>();

        for (BoardComment boardComment : boardComments) {
            //해당 게시글을 보는 viewer 와 해당 댓글의 작성자와 같은지 확인(수정 또는 삭제를 위함)
            Boolean isAllowed = (viewer != null && viewer.getId()
                .equals(boardComment.getMember().getId()));
            boardCommentSimpleInfoList.add(
                BoardCommentSimpleInfo.builder()
                    .boardComment(boardComment)
                    .createdAt(calculateTime(boardComment.getCreatedAt(), 3))
                    .isAllowed(isAllowed)
                    .isLiked(boardCommentLikeRepository.existsBoardCommentLikeByMemberAndStateIsTrueAndBoardCommentId(
                        boardComment.getMember(), boardComment.getId())) //댓글 좋아요 눌렀는지 안눌렀는지 확인
                    .memberSimpleInfo(
                        new MemberSimpleInfo(
                            boardComment.getMember().getId(),
                            boardComment.getMember().getNickName(),
                            boardComment.getMember().getDetailMbti(),
                            badgeRepository.findNameMemberAndStateTrue(boardComment.getMember())
                                .orElse(null),
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

        Page<BoardComment> result = boardCommentRepository.findAllByBoardIdAndStateIsTrue(boardId,
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


}
