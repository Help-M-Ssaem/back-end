package com.example.mssaem_backend.domain.board;

import static com.example.mssaem_backend.global.common.CheckWriter.isMatch;
import static com.example.mssaem_backend.global.common.CheckWriter.match;
import static com.example.mssaem_backend.global.common.Time.calculateTime;

import com.example.mssaem_backend.domain.board.dto.BoardRequestDto.PatchBoardReq;
import com.example.mssaem_backend.domain.board.dto.BoardRequestDto.PostBoardReq;
import com.example.mssaem_backend.domain.board.dto.BoardResponseDto.BoardHistory;
import com.example.mssaem_backend.domain.board.dto.BoardResponseDto.BoardList;
import com.example.mssaem_backend.domain.board.dto.BoardResponseDto.BoardSimpleInfo;
import com.example.mssaem_backend.domain.board.dto.BoardResponseDto.GetBoardRes;
import com.example.mssaem_backend.domain.board.dto.BoardResponseDto.ThreeHotInfo;
import com.example.mssaem_backend.domain.boardcomment.BoardCommentRepository;
import com.example.mssaem_backend.domain.boardimage.BoardImageService;
import com.example.mssaem_backend.domain.discussion.Discussion;
import com.example.mssaem_backend.domain.discussion.DiscussionService;
import com.example.mssaem_backend.domain.like.LikeRepository;
import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import com.example.mssaem_backend.domain.worryboard.WorryBoardRepository;
import com.example.mssaem_backend.global.common.CommentService;
import com.example.mssaem_backend.global.common.CommentTypeEnum;
import com.example.mssaem_backend.global.common.Time;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.BoardErrorCode;
import jakarta.transaction.Transactional;
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
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardImageService boardImageService;
    private final LikeRepository likeRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final WorryBoardRepository worryBoardRepository;
    private final CommentService commentService;
    private final DiscussionService discussionService;

    private static final Long likeCountStandard = 10L; // HOT 게시글 기준이 되는 좋아요수

    // HOT 게시물 더보기
    public PageResponseDto<List<BoardSimpleInfo>> findHotBoardList(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Board> boards =
            boardRepository.findBoardsByLikeCountGreaterThanEqualAndStateIsTrueOrderByCreatedAtDesc(
                likeCountStandard,
                pageRequest
            );

        return new PageResponseDto<>(
            boards.getNumber(),
            boards.getTotalPages(),
            setBoardSimpleInfo(
                boards.stream().collect(Collectors.toList()),
                3)
        );
    }

    // 홈 화면 - 최상위 제외한 HOT 게시물 4개만 조회
    public List<BoardSimpleInfo> findHotBoardListForHome() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<Board> boards =
            boardRepository.findBoardsByLikeCountGreaterThanEqualAndStateIsTrueOrderByCreatedAtDesc(
                    likeCountStandard,
                    pageRequest)
                .stream().collect(Collectors.toList());

        if (!boards.isEmpty()) {
            boards.remove(0);
        }
        return setBoardSimpleInfo(boards, 1);
    }

    // 홈 화면 조회 - HOT 게시물, HOT 토론, 가장 최신 고민글 조회
    public ThreeHotInfo findThreeHotForHome() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        List<Board> boards =
            boardRepository.findBoardsByLikeCountGreaterThanEqualAndStateIsTrueOrderByCreatedAtDesc(
                    likeCountStandard,
                    pageRequest)
                .stream().toList();
        List<Discussion> discussions = discussionService.findHotDiscussions(pageRequest)
            .stream().toList();
        WorryBoard worryBoard = worryBoardRepository.findTopByStateTrueAndIsSolvedFalseOrderByCreatedAtDesc();

        return ThreeHotInfo.builder()
            .boardId(!boards.isEmpty() ? boards.get(0).getId() : null)
            .boardTitle(!boards.isEmpty() ? boards.get(0).getTitle() : null)
            .discussionId(!discussions.isEmpty() ? discussions.get(0).getId() : null)
            .discussionTitle(!discussions.isEmpty() ? discussions.get(0).getTitle() : null)
            .worryBoardId(worryBoard != null ? worryBoard.getId() : null)
            .worryBoardTitle(worryBoard != null ? worryBoard.getTitle() : null)
            .build();
    }


    // 게시물 전체 조회시 각 게시물의 간단한 정보 Dto에 매핑
    private List<BoardSimpleInfo> setBoardSimpleInfo(List<Board> boards, int dateType) {
        List<BoardSimpleInfo> boardSimpleInfos = new ArrayList<>();

        for (Board board : boards) {
            boardSimpleInfos.add(
                new BoardSimpleInfo(
                    board, new MemberSimpleInfo(board.getMember()),
                    Time.calculateTime(board.getCreatedAt(), dateType)
                )
            );
        }
        return boardSimpleInfos;
    }

    @Transactional
    public String createBoard(Member member, PostBoardReq postBoardReq,
        List<String> imgUrls, List<String> uploadImgUrls) {

        Board board = Board.builder()
            .title(postBoardReq.getTitle())
            .content(postBoardReq.getContent())
            .mbti(postBoardReq.getMbti())
            .member(member)
            .thumbnail(null)
            .build();

        if (uploadImgUrls != null) {
            //최종 upload Image Urls 들만 DB에 저장
            String thumbnail = boardImageService.uploadBoardImageUrl(board, uploadImgUrls);
            board.changeThumbnail(thumbnail);
        }
        boardRepository.save(board);

        if (!imgUrls.isEmpty()) {
            //차집합을 통해 s3 에 업로드 된 모든 사진 차집합 통해 삭제
            imgUrls.removeAll(uploadImgUrls);
            boardImageService.deleteBoardImageUrl(imgUrls);
        }
        return "게시글 생성 완료";
    }

    @Transactional
    public String modifyBoard(Member member, PatchBoardReq patchBoardReq, Long boardId,
        List<String> imgUrls, List<String> uploadImgUrls) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new BaseException(BoardErrorCode.EMPTY_BOARD));
        //현재 로그인한 멤버와 해당 게시글의 멤버가 같은지 확인
        if (isMatch(member, board.getMember())) {
            board.modifyBoard(patchBoardReq.getTitle(), patchBoardReq.getContent(),
                patchBoardReq.getMbti());
            board.changeThumbnail(
                boardImageService.modifyBoardImageUrl(board, imgUrls, uploadImgUrls));
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
            match(member, board.getMember());
            //현재 저장된 이미지 삭제
            boardImageService.deleteBoardImage(board);
            //해당 게시글 좋아요 삭제
            likeRepository.deleteAllByBoard(board);
            //게시글 삭제 시 모든 댓글 삭제, 댓글 좋아요 삭제
            commentService.deleteAllComments(boardId, CommentTypeEnum.BOARD);
            //게시글 Soft Delete
            board.deleteBoard();
            return "게시글 삭제 완료";
        } else {
            throw new BaseException(BoardErrorCode.EMPTY_BOARD);
        }
    }

    //게시글 전체 조회 , 게시글 상세 조회시 boardId 입력 받아 현재 게시글 제외하고 전체 조회
    public PageResponseDto<List<BoardSimpleInfo>> findBoards(int page, int size, Long boardId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Board> result = boardRepository.findAllByStateIsTrueAndIdOrderByCreatedAtDesc(boardId,
            pageable);
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
        Page<Board> result = boardRepository.findAllByStateIsTrueAndMbtiOrderByCreatedAtDesc(mbti,
            pageable);
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
        Page<Board> result = boardRepository.findAllByMemberIdAndStateIsTrueOrderByCreatedAtDesc(id,
            pageable);
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
    @Transactional
    public GetBoardRes findBoardById(Member viewer, Long id) {
        Board board = boardRepository.findByIdAndStateIsTrue(id)
            .orElseThrow(() -> new BaseException(BoardErrorCode.EMPTY_BOARD));
        Member member = board.getMember();
        //게시글 수정, 삭제 권한 확인
        Boolean isAllowed = (isMatch(viewer, member));
        //게시글 좋아요 눌렀는지 확인
        Boolean isLiked = likeRepository.existsLikeByMemberAndStateIsTrueAndBoard(viewer, board);
        //게시글 상세 조회 시 조회수 상승
        board.increaseHits();

        return GetBoardRes.builder()
            .memberSimpleInfo(
                new MemberSimpleInfo(
                    board.getMember().getId(),
                    board.getMember().getNickName(),
                    board.getMember().getDetailMbti(),
                    board.getMember().getBadgeName(),
                    board.getMember().getProfileImageUrl()
                )
            )
            .board(board)
            .imgUrlList(boardImageService.getImgUrls(board))
            .createdAt(calculateTime(board.getCreatedAt(), 2))
            .commentCount(boardCommentRepository.countByBoardAndStateTrue(board))
            .isAllowed(isAllowed)
            .isLiked(isLiked)
            .build();
    }

    // Mbti 카테고리 별 검색하기
    public PageResponseDto<List<BoardSimpleInfo>> findBoardListByKeywordAndMbti(
        int searchType, String keyword, String strMbti, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        MbtiEnum mbti = strMbti.equals("ALL") ? null : MbtiEnum.valueOf(strMbti);

        Page<Board> boards = boardRepository.searchByTypeAndMbti(searchType,
            keyword, mbti, pageRequest);

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

    public BoardHistory getBoardHistory(Member member) {
        return new BoardHistory(
            boardRepository.countAllByStateIsTrueAndMember(member),
            boardCommentRepository.countAllByStateIsTrueAndMember(member),
            boardRepository.sumLikeCountByMember(member).orElse(0L)
        );
    }

    //게시판 별 게시글 개수 조회
    public BoardList getBoardList() {
        //BoardList 객체 생성
        BoardList boardList = new BoardList();
        //전체 게시글 개수
        boardList.setBoardCount(boardRepository.countAllByStateIsTrue());
        //MBTI별 게시글 개수
        List<Object[]> boardCountList = boardRepository.countBoardsByMbtiAndStateIsTrue();
        //조회한 데이터를 해당하는 MBTI에 할당
        for (Object[] row : boardCountList) {
            MbtiEnum mbti = (MbtiEnum) row[0];
            Long count = (Long) row[1];

            switch (mbti) {
                case INFJ -> boardList.setINFJ(count);
                case INFP -> boardList.setINFP(count);
                case ISFJ -> boardList.setISFJ(count);
                case ISFP -> boardList.setISFP(count);
                case ISTP -> boardList.setISTP(count);
                case ISTJ -> boardList.setISTJ(count);
                case INTP -> boardList.setINTP(count);
                case INTJ -> boardList.setINTJ(count);
                case ENTP -> boardList.setENTP(count);
                case ESTJ -> boardList.setESTJ(count);
                case ESTP -> boardList.setESTP(count);
                case ENFP -> boardList.setENFP(count);
                case ESFJ -> boardList.setESFJ(count);
                case ENTJ -> boardList.setENTJ(count);
                case ENFJ -> boardList.setENFJ(count);
                case ESFP -> boardList.setESFP(count);
            }
        }
        return boardList;
    }

}
