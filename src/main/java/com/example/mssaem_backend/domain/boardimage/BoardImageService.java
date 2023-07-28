package com.example.mssaem_backend.domain.boardimage;

import com.example.mssaem_backend.domain.board.Board;
import com.example.mssaem_backend.global.s3.S3Service;
import com.example.mssaem_backend.global.s3.dto.S3Result;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class BoardImageService {

    private final BoardImageRepository boardImageRepository;
    private final S3Service s3Service;

    public List<BoardImage> loadImage(Long boardId) {
        return boardImageRepository.findAllByBoardId(boardId);
    }

    public void deleteImage(Board board) {
        boardImageRepository.deleteAllByBoard(board);
    }

    public String uploadBoardImage(Board board, List<MultipartFile> multipartFiles) {
        //S3에 먼저 저장된 리스트를 받아와 DB에 이미지 저장
        List<S3Result> s3ResultList = s3Service.uploadFile(multipartFiles);

        List<BoardImage> boardImages = new ArrayList<>();

        if (!s3ResultList.isEmpty()) {
            for (S3Result s3Result : s3ResultList) {
                boardImages.add(new BoardImage(board, s3Result.getImgUrl()));
            }
        }
        boardImageRepository.saveAll(boardImages);

        return s3ResultList.isEmpty() ? null : s3ResultList.get(0).getImgUrl();
    }

    public void deleteBoardImage(Board board) {
        //현재 DB에 저장된 이미지 불러오기
        List<BoardImage> dbBoardImageList = loadImage(board.getId());
        //S3 삭제
        for (BoardImage boardImage : dbBoardImageList) {
            s3Service.deleteFile(s3Service.parseFileName(boardImage.getImageUrl()));
        }
        //DB에 저장된 이미지 삭제
        deleteImage(board);
    }

    //해당 게시글 이미지 url 리스트 가져오기
    public List<String> getImgUrls(Board board) {
        List<BoardImage> boardImageList = boardImageRepository.findAllByBoardId(board.getId());

        if (boardImageList.isEmpty()) {
            return Collections.singletonList("default");
        }
        return boardImageList.stream()
            .map(BoardImage::getImageUrl)
            .collect(Collectors.toList());
    }
}
