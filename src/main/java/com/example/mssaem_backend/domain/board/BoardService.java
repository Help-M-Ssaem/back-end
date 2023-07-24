package com.example.mssaem_backend.domain.board;

import com.example.mssaem_backend.domain.badge.Badge;
import com.example.mssaem_backend.domain.badge.BadgeRepository;
import com.example.mssaem_backend.domain.board.dto.BoardRequestDto.PatchBoardReq;
import com.example.mssaem_backend.domain.board.dto.BoardRequestDto.PostBoardReq;
import com.example.mssaem_backend.domain.board.dto.BoardResponseDto.BoardSimpleInfo;
import com.example.mssaem_backend.domain.board.dto.BoardResponseDto.ThreeHotInfo;
import com.example.mssaem_backend.domain.boardcomment.BoardCommentRepository;
import com.example.mssaem_backend.domain.boardimage.BoardImage;
import com.example.mssaem_backend.domain.boardimage.BoardImageRepository;
import com.example.mssaem_backend.domain.boardimage.BoardImageService;
import com.example.mssaem_backend.domain.discussion.Discussion;
import com.example.mssaem_backend.domain.discussion.DiscussionRepository;
import com.example.mssaem_backend.domain.like.LikeRepository;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import com.example.mssaem_backend.domain.worryboard.WorryBoardRepository;
import com.example.mssaem_backend.global.common.Time;
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
    private final DiscussionRepository discussionRepository;
    private final WorryBoardRepository worryBoardRepository;

    // HOT 게시물 더보기
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
                    .collect(Collectors.toList()),
                3)
        );
    }

    // 홈 화면 - 최상위 제외한 HOT 게시물 4개만 조회
    public List<BoardSimpleInfo> findHotBoardListForHome() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<Board> boards =
            likeRepository.findBoardsWithMoreThanTenLikesInLastThreeDaysAndStateTrue(
                    LocalDateTime.now().minusDays(3),
                    pageRequest
                ).stream()
                .collect(Collectors.toList());
        if (!boards.isEmpty()) {
            boards.remove(0);
        }

        return setBoardSimpleInfo(boards, 1);
    }


    // 게시물 전체 조회시 각 게시물의 간단한 정보 Dto에 매핑
    private List<BoardSimpleInfo> setBoardSimpleInfo(List<Board> boards, int dateType) {
        List<BoardSimpleInfo> boardSimpleInfos = new ArrayList<>();

        for (Board board : boards) {
            boardSimpleInfos.add(
                new BoardSimpleInfo(
                    board.getId(),
                    board.getTitle(),
                    board.getContent(),
                    boardImageRepository.findImageUrlByBoardOrderById(board).orElse(null),
                    board.getMbti(),
                    board.getLikeCount(),
                    boardCommentRepository.countByBoardAndStateTrue(board),
                    Time.calculateTime(board.getCreatedAt(), dateType),
                    new MemberSimpleInfo(
                        board.getMember().getId(),
                        board.getMember().getNickName(),
                        board.getMember().getDetailMbti(),
                        badgeRepository.findNameMemberAndStateTrue(board.getMember()).orElse(null),
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
            .orElseThrow(() -> new BaseException(BoardErrorCode.EMPTY_BOARD));
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
            throw new BaseException(BoardErrorCode.EMPTY_BOARD);
        }
    }

    // 홈 화면에 보여줄 HOT 게시물, HOT 토론, 가장 최신 고민글 조회
    public ThreeHotInfo findThreeHotForHome() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        List<Board> boards =
            likeRepository.findBoardsWithMoreThanTenLikesInLastThreeDaysAndStateTrue(
                    LocalDateTime.now().minusDays(3)
                    , pageRequest
                )
                .stream().toList();
        List<Discussion> discussions =
            discussionRepository.findDiscussionWithMoreThanTenParticipantsInLastThreeDaysAndStateTrue(
                LocalDateTime.now().minusDays(3)
                , pageRequest
            ).stream().toList();
        WorryBoard worryBoard = worryBoardRepository.findTopByStateFalseOrderByCreatedAtDesc();

        return ThreeHotInfo.builder()
            .boardId(!boards.isEmpty() ? boards.get(0).getId() : null)
            .boardTitle(!boards.isEmpty() ? boards.get(0).getTitle() : null)
            .discussionId(!discussions.isEmpty() ? discussions.get(0).getId() : null)
            .discussionTitle(!discussions.isEmpty() ? discussions.get(0).getTitle() : null)
            .worryBoardId(worryBoard != null ? worryBoard.getId() : null)
            .worryBoardTitle(worryBoard != null ? worryBoard.getTitle() : null)
            .build();

    }


    //게시글 전체 조회 , 게시글 상세 조회시 boardId 입력 받아 현재 게시글 제외하고 전체 조회
    public PageResponseDto<List<BoardSimpleInfo>> findBoards(int page, int size, Long boardId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Board> result = boardRepository.findAllByStateIsTrueAndId(boardId, pageable);
        return new PageResponseDto<>(
            result.getNumber(),
            result.getTotalPages(),
            setBoardSimpleInfo(
                result
                    .stream()
                    .collect(Collectors.toList()), 3)
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
                    .collect(Collectors.toList()), 3)
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
                    .collect(Collectors.toList()), 3)
        );
    }

    //게시글 상세 조회
    public GetBoardRes findBoardById(Member viewer, Long id) {
        Board board = boardRepository.findById(id)
            .orElseThrow(() -> new BaseException(BoardErrorCode.EMPTY_BOARD));
        Member member = board.getMember();
        //게시글 수정, 삭제 권한 확인
        Boolean isAllowed = (viewer != null && viewer.getId().equals(member.getId()));

        return GetBoardRes.builder()
            .memberSimpleInfo(
                new MemberSimpleInfo(
                    board.getMember().getId(),
                    board.getMember().getNickName(),
                    board.getMember().getDetailMbti(),
                    badgeRepository.findNameMemberAndStateTrue(board.getMember()).orElse(null),
                    board.getMember().getProfileImageUrl()
                )
            )
            .board(board)
            .imgUrlList(boardImageService.getImgUrls(board))
            .createdAt(calculateTime(board.getCreatedAt(), 2))
            .commentCount(boardCommentRepository.countByBoardAndStateTrue(board))
            .isAllowed(isAllowed)
            .build();
    }
}
