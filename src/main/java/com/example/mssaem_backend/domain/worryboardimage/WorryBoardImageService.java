package com.example.mssaem_backend.domain.worryboardimage;

import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import com.example.mssaem_backend.global.s3.S3Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class WorryBoardImageService {

    private final WorryBoardImageRepository worryBoardImageRepository;
    private final S3Service s3Service;

    //해당 고민글 이미지 url 리스트 가져오기
    public List<String> getImgUrls(WorryBoard worryBoard) {
        List<WorryBoardImage> worryBoardImages = worryBoardImageRepository.findAllByWorryBoard(
            worryBoard);

        if (worryBoardImages.isEmpty()) {
            return Collections.singletonList("default");
        }
        return worryBoardImages.stream().map(WorryBoardImage::getImgUrl)
            .collect(Collectors.toList());
    }

    //해당 고민글 대표 이미지 가져오기
    public String getImgUrl(WorryBoard worryBoard) {
        WorryBoardImage worryBoardImage = worryBoardImageRepository.findTopByWorryBoardOrderById(
            worryBoard);
        if (worryBoardImage == null) {
            return "default";
        }
        return worryBoardImage.getImgUrl();
    }

    //전달받은 imgUrl 리스트 DB에 저장
    public String uploadWorryBoardImageUrl(WorryBoard worryboard, List<String> imgUrls) {
        List<WorryBoardImage> worryBoardImages = new ArrayList<>();

        if (!imgUrls.isEmpty()) {
            for (String result : imgUrls) {
                worryBoardImages.add(new WorryBoardImage(worryboard, result));
            }
        }
        worryBoardImageRepository.saveAll(worryBoardImages);

        return imgUrls.isEmpty() ? null : imgUrls.get(0);
    }

    public void deleteWorryBoardImage(WorryBoard worryBoard) {
        List<WorryBoardImage> worryBoardImages = worryBoardImageRepository.findAllByWorryBoard(
            worryBoard);
        //s3 삭제
        for (WorryBoardImage worryBoardimage : worryBoardImages) {
            s3Service.deleteFile(s3Service.parseFileName(worryBoardimage.getImgUrl()));
        }
        worryBoardImageRepository.deleteAllByWorryBoard(worryBoard);
    }

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        return s3Service.uploadImage(multipartFile);
    }

    public void deleteWorryBoardImageUrl(List<String> imgUrls) {
        //업로드할 때 S3에 저장된 이미지들을 모두 삭제
        for (String imgUrl : imgUrls) {
            s3Service.deleteFile(s3Service.parseFileName(imgUrl));
        }
    }

    public String modifyWorryBoardImageUrl(WorryBoard worryboard, List<String> imgUrls,
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
            worryBoardImageRepository.deleteWorryBoardImageByImgUrl(imgUrl);
        }

        // 새로운 이미지들을 저장합니다.
        for (String uploadImgUrl : newUploadImgUrls) {
            worryBoardImageRepository.save(new WorryBoardImage(worryboard, uploadImgUrl));
        }

        return uploadImgUrls.isEmpty() ? null : uploadImgUrls.get(0);
    }
}