package com.example.mssaem_backend.domain.board;

import com.example.mssaem_backend.domain.badge.Badge;
import com.example.mssaem_backend.domain.badge.BadgeRepository;
import com.example.mssaem_backend.domain.board.dto.BoardRequestDto.DeleteBoardReq;
import com.example.mssaem_backend.domain.board.dto.BoardRequestDto.PatchBoardReq;
import com.example.mssaem_backend.domain.board.dto.BoardRequestDto.PostBoardReq;
import com.example.mssaem_backend.domain.board.dto.BoardResponseDto.BoardSimpleInfo;
import com.example.mssaem_backend.domain.boardcomment.BoardCommentRepository;
import com.example.mssaem_backend.domain.boardimage.BoardImage;
import com.example.mssaem_backend.domain.boardimage.BoardImageRepository;
import com.example.mssaem_backend.domain.boardimage.BoardImageService;
import com.example.mssaem_backend.domain.like.LikeRepository;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.global.common.Time;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.BoardErrorCode;
import com.example.mssaem_backend.global.s3.S3Service;
import com.example.mssaem_backend.global.s3.dto.S3Result;
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
    private final S3Service s3Service;
    private final BoardImageService boardImageService;
    private final LikeRepository likeRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final BadgeRepository badgeRepository;
    private final BoardImageRepository boardImageRepository;

    public PageResponseDto<List<BoardSimpleInfo>> findHotBoardList(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Board> boards =
            likeRepository.findBoardsWithMoreThanTenLikesInLastThreeDays(
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
            likeRepository.findBoardsWithMoreThanTenLikesInLastThreeDays(
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
                    board.getRecommendation(),
                    boardCommentRepository.countByBoardAndState(board, true),
                    Time.calculateTime(board.getCreatedAt(), 3),
                    new MemberSimpleInfo(
                        board.getMember().getId(),
                        board.getMember().getNickName(),
                        board.getMember().getMbti(),
                        badgeRepository.findBadgeByMemberAndState(board.getMember(), true)
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
            uploadBoardImage(board, multipartFiles);
        }
        return "게시글 생성 완료";
    }

    @Transactional
    public String modifyBoard(Member member, PatchBoardReq patchBoardReq, Long boardId,
        List<MultipartFile> multipartFiles) {
        if (member.getId() == patchBoardReq.getMemberId()) {
            Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BaseException(BoardErrorCode.BOARD_NOT_FOUND));
            board.modifyBoard(patchBoardReq.getTitle(), patchBoardReq.getContent(),
                patchBoardReq.getMbti());
            //현재 저장된 이미지 삭제
            deleteBoardImage(board);
            //새로운 이미지 업로드
            if (multipartFiles != null) {
                uploadBoardImage(board, multipartFiles);
            }
            return "게시글 수정 완료";
        } else {
            throw new BaseException(BoardErrorCode.INVALID_MEMBER);
        }
    }

    @Transactional
    public String deleteBoard(Member member, DeleteBoardReq deleteBoardReq, Long boardId) {
        if (member.getId() != null && member.getId() == deleteBoardReq.getMemberId()) {
            Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BaseException(BoardErrorCode.BOARD_NOT_FOUND));
            //게시글 Soft Delete
            board.deleteBoard();
            //현재 저장된 이미지 삭제
            deleteBoardImage(board);
            return "게시글 삭제 완료";
        } else {
            throw new BaseException(BoardErrorCode.INVALID_MEMBER);
        }
    }

    private void uploadBoardImage(Board board, List<MultipartFile> multipartFiles) {
        //multipartFiles 로 부터 파일 받아오기
        List<S3Result> boardImageList = s3Service.uploadFile(multipartFiles);
        //이미지 저장
        if (!boardImageList.isEmpty()) {
            for (S3Result s3Result : boardImageList) {
                boardImageService.uploadImage(s3Result.getImgUrl(), board);
            }
        }
    }

    private void deleteBoardImage(Board board) {
        //현재 DB에 저장된 이미지 불러오기
        List<BoardImage> dbBoardImageList = boardImageService.loadImage(board.getId());
        //S3 삭제
        for (BoardImage boardImage : dbBoardImageList) {
            s3Service.deleteFile(s3Service.parseFileName(boardImage.getImageUrl()));
        }
        //DB에 저장된 이미지 삭제
        boardImageService.deleteImage(board);
    }

}
