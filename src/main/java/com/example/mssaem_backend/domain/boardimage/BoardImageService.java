package com.example.mssaem_backend.domain.boardimage;

import com.example.mssaem_backend.domain.board.Board;
import com.example.mssaem_backend.global.s3.S3Service;
import com.example.mssaem_backend.global.s3.dto.S3Result;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class BoardImageService {

    private final BoardImageRepository boardImageRepository;
    private final S3Service s3Service;

    public String uploadImage(String boardImageUrl, Board board) {
        BoardImage boardImage = new BoardImage(boardImageUrl);
        boardImage.setBoard(board);
        boardImageRepository.save(boardImage);
        return "이미지 업로드 완료";
    }

    public List<BoardImage> loadImage(Long boardId) {
        return boardImageRepository.findAllByBoardId(boardId);
    }

    public void deleteImage(Board board) {
        boardImageRepository.deleteAllByBoard(board);
    }

    public void uploadBoardImage(Board board, List<MultipartFile> multipartFiles) {
        //multipartFiles 로 부터 파일 받아오기
        List<S3Result> boardImageList = s3Service.uploadFile(multipartFiles);
        //이미지 저장
        if (!boardImageList.isEmpty()) {
            for (S3Result s3Result : boardImageList) {
                uploadImage(s3Result.getImgUrl(), board);
            }
        }
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
}
