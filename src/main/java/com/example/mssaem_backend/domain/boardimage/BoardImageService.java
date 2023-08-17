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

    public void deleteBoardImageUrl(List<String> imgUrls) {
        //업로드할 때 S3에 저장된 이미지들을 모두 삭제
        for (String imgUrl : imgUrls) {
            s3Service.deleteFile(s3Service.parseFileName(imgUrl));
        }
    }

    public String modifyBoardImageUrl(Board board, List<String> imgUrls,
        List<String> uploadImgUrls) {
        List<String> imgUrlsToDelete = new ArrayList<>();
        List<String> newUploadImgUrls = new ArrayList<>();

        if (!imgUrls.isEmpty()) { // 기존에 사진이 있을 경우
            imgUrlsToDelete = new ArrayList<>(imgUrls);
            imgUrlsToDelete.removeAll(uploadImgUrls); // 차집합 이용

            if (!uploadImgUrls.isEmpty()) { // 새로 업로드 된 이미지가 있다면 비교해서 저장
                newUploadImgUrls = new ArrayList<>(uploadImgUrls);
                newUploadImgUrls.removeAll(imgUrls); // 차집합 이용
            }
        } else if (!uploadImgUrls.isEmpty()) { // 기존 사진이 없지만 새로 업로드된 이미지가 있는 경우
            newUploadImgUrls = new ArrayList<>(uploadImgUrls);
        }

        // 기존 이미지들 중 삭제가 필요한 이미지들을 삭제
        for (String imgUrl : imgUrlsToDelete) {
            s3Service.deleteFile(s3Service.parseFileName(imgUrl));
            boardImageRepository.deleteBoardImageByImageUrl(imgUrl);
        }

        // 새로운 이미지들을 저장합니다.
        for (String uploadImgUrl : newUploadImgUrls) {
            boardImageRepository.save(new BoardImage(board, uploadImgUrl));
        }

        return uploadImgUrls.isEmpty() ? null : uploadImgUrls.get(0);
    }
}
