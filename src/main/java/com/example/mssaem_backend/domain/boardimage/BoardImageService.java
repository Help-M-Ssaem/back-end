package com.example.mssaem_backend.domain.boardimage;

import com.example.mssaem_backend.domain.board.Board;
import com.example.mssaem_backend.global.s3.S3Service;
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

    //이미지 S3에 저장 후 해당 파일에 대한 url 바로 반환
    public String uploadFile(MultipartFile multipartFile) {
        return s3Service.uploadImage(multipartFile);
    }

    //전달받은 imgUrl 리스트 DB에 저장
    public String uploadBoardImageUrl(Board board, List<String> imgUrls) {
        List<BoardImage> boardImages = new ArrayList<>();

        if (!imgUrls.isEmpty()) {
            for (String result : imgUrls) {
                boardImages.add(new BoardImage(board, result));
            }
        }
        boardImageRepository.saveAll(boardImages);

        return imgUrls.isEmpty() ? null : imgUrls.get(0);
    }
}
