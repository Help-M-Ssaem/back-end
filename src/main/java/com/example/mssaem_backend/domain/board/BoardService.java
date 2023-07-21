package com.example.mssaem_backend.domain.board;

import static com.example.mssaem_backend.global.common.Time.calculateTime;

import com.example.mssaem_backend.domain.badge.Badge;
import com.example.mssaem_backend.domain.badge.BadgeRepository;
import com.example.mssaem_backend.domain.board.dto.BoardRequestDto.PatchBoardReq;
import com.example.mssaem_backend.domain.board.dto.BoardRequestDto.PostBoardReq;
import com.example.mssaem_backend.domain.board.dto.BoardResponseDto.BoardSimpleInfo;
import com.example.mssaem_backend.domain.board.dto.BoardResponseDto.GetBoardRes;
import com.example.mssaem_backend.domain.boardcomment.BoardCommentRepository;
import com.example.mssaem_backend.domain.boardcomment.BoardCommentService;
import com.example.mssaem_backend.domain.boardimage.BoardImage;
import com.example.mssaem_backend.domain.boardimage.BoardImageRepository;
import com.example.mssaem_backend.domain.boardimage.BoardImageService;
import com.example.mssaem_backend.domain.like.LikeRepository;
import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.BoardErrorCode;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardImageService boardImageService;
    private final LikeRepository likeRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final BadgeRepository badgeRepository;
    private final BoardImageRepository boardImageRepository;
    private final BoardCommentService boardCommentService;

    public PageResponseDto<List<BoardSimpleInfo>> findHotBoardList(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Board> boards =
            likeRepository.findBoardsWithMoreThanTenLikesInLastThreeDaysAndStateTrue(
                LocalDateTime.now().minusDays(3),
                pageRequest
            );

        return new PageResponseDto<>(
            boards.getNumber(),
            boards.getTotalPages(),
            setBoardSimpleInfo(
                boards
                    .stream()
                    .collect(Collectors.toList()))
        );
    }

    public List<BoardSimpleInfo> findHotBoardListForHome() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<Board> boards =
            likeRepository.findBoardsWithMoreThanTenLikesInLastThreeDaysAndStateTrue(
                    LocalDateTime.now().minusDays(3)
                    , pageRequest
                )
                .stream()
                .collect(Collectors.toList());
        if (!boards.isEmpty()) {
            boards.remove(0);
        }

        return setBoardSimpleInfo(boards);
    }

    private List<BoardSimpleInfo> setBoardSimpleInfo(List<Board> boards) {
        List<BoardSimpleInfo> boardSimpleInfos = new ArrayList<>();

        for (Board board : boards) {
            boardSimpleInfos.add(
                new BoardSimpleInfo(
                    board.getId(),
                    board.getTitle(),
                    board.getContent(),
                    boardImageRepository.findTopByBoardOrderById(board).orElse(new BoardImage())
                        .getImageUrl(),
                    board.getMbti(),
                    board.getLikeCount(),
                    boardCommentRepository.countWithStateTrueByBoard(board),
                    calculateTime(board.getCreatedAt(), 3),
                    new MemberSimpleInfo(
                        board.getMember().getId(),
                        board.getMember().getNickName(),
                        board.getMember().getMbti(),
                        badgeRepository.findBadgeWithStateTrueByMember(board.getMember())
                            .orElse(new Badge()).getName(),
                        board.getMember().getProfileImageUrl()
                    )
                )
            );
        }
        return boardSimpleInfos;
    }

    public String createBoard(Member member, PostBoardReq postBoardReq,
        List<MultipartFile> multipartFiles) {
        Board board = Board.builder()
            .title(postBoardReq.getTitle())
            .content(postBoardReq.getContent())
            .mbti(postBoardReq.getMbti())
            .member(member)
            .build();
        boardRepository.save(board);
        if (multipartFiles != null) {
            boardImageService.uploadBoardImage(board, multipartFiles);
        }
        return "게시글 생성 완료";
    }

    @Transactional
    public String modifyBoard(Member member, PatchBoardReq patchBoardReq, Long boardId,
        List<MultipartFile> multipartFiles) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new BaseException(BoardErrorCode.BOARD_NOT_FOUND));
        //현재 로그인한 멤버와 해당 게시글의 멤버가 같은지 확인
        if (member.getId().equals(board.getMember().getId())) {
            board.modifyBoard(patchBoardReq.getTitle(), patchBoardReq.getContent(),
                patchBoardReq.getMbti());
            //현재 저장된 이미지 삭제
            boardImageService.deleteBoardImage(board);
            //새로운 이미지 업로드
            if (multipartFiles != null) {
                boardImageService.uploadBoardImage(board, multipartFiles);
            }
            return "게시글 수정 완료";
        } else {
            throw new BaseException(BoardErrorCode.INVALID_MEMBER);
        }
    }

    @Transactional
    public String deleteBoard(Member member, Long boardId) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new BaseException(BoardErrorCode.BOARD_NOT_FOUND));
        if (board.isState()) {
            //현재 로그인한 멤버와 해당 게시글의 멤버가 같은지 확인
            if (member.getId().equals(board.getMember().getId())) {
                //게시글 Soft Delete
                board.deleteBoard();
                //현재 저장된 이미지 삭제
                boardImageService.deleteBoardImage(board);
                return "게시글 삭제 완료";
            } else {
                throw new BaseException(BoardErrorCode.INVALID_MEMBER);
            }
        } else {
            throw new BaseException(BoardErrorCode.BOARD_NOT_FOUND);
        }
    }


    //게시글 전체 조회
    public PageResponseDto<List<BoardSimpleInfo>> findBoards(int page, int size , Long boardId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Board> result = boardRepository.findAllByStateIsTrueAndId(boardId , pageable);
        return new PageResponseDto<>(
            result.getNumber(),
            result.getTotalPages(),
            setBoardSimpleInfo(
                result
                    .stream()
                    .collect(Collectors.toList()))
        );
    }

    //Mbti 카테고리 별 게시글 전체 조회
    public PageResponseDto<List<BoardSimpleInfo>> findBoardsByMbti(MbtiEnum mbti, int page,
        int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Board> result = boardRepository.findAllByStateIsTrueAndMbti(mbti, pageable);
        return new PageResponseDto<>(
            result.getNumber(),
            result.getTotalPages(),
            setBoardSimpleInfo(
                result
                    .stream()
                    .collect(Collectors.toList()))
        );
    }

    //특정 멤버별 게시글 전체 조회
    public PageResponseDto<List<BoardSimpleInfo>> findBoardsByMemberId(Long id, int page,
        int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Board> result = boardRepository.findAllByMemberIdAndStateIsTrue(id, pageable);
        return new PageResponseDto<>(
            result.getNumber(),
            result.getTotalPages(),
            setBoardSimpleInfo(
                result
                    .stream()
                    .collect(Collectors.toList()))
        );
    }

    //게시글 상세 조회
    public GetBoardRes findBoardById(Member viewer, Long id) {
        Board board = boardRepository.findById(id)
            .orElseThrow(() -> new BaseException(BoardErrorCode.BOARD_NOT_FOUND));
        Member member = board.getMember();
        //게시글 수정, 삭제 권한 확인
        Boolean isAllowed = (viewer != null && viewer.getId().equals(member.getId()));

        return GetBoardRes.builder()
            .memberSimpleInfo(
                new MemberSimpleInfo(
                    board.getMember().getId(),
                    board.getMember().getNickName(),
                    board.getMember().getMbti(),
                    badgeRepository.findBadgeWithStateTrueByMember(board.getMember())
                        .orElse(new Badge()).getName(),
                    board.getMember().getProfileImageUrl()
                )
            )
            .board(board)
            .imgUrlList(boardImageService.getImgUrls(board))
            .createdAt(calculateTime(board.getCreatedAt(), 2))
            .commentCount(boardCommentRepository.countWithStateTrueByBoard(board))
            .isAllowed(isAllowed)
            .boardCommentSimpleInfo(boardCommentService.setBoardCommentSimpleInfo(
                boardCommentService.boardCommentList(board.getId())))
            .build();
    }
}
