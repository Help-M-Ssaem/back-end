package com.example.mssaem_backend.domain.worryboardimage;

import com.example.mssaem_backend.domain.worryboard.WorryBoard;
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

        if(!imgUrls.isEmpty()) {
            for(String result : imgUrls) {
                worryBoardImages.add(new WorryBoardImage(worryboard, result));
            }
        }
        worryBoardImageRepository.saveAll(worryBoardImages);

        return imgUrls.isEmpty() ? null : imgUrls.get(0);
    }

    public void deleteWorryBoardImage(WorryBoard worryBoard) {
        List<WorryBoardImage> worryBoardImages = worryBoardImageRepository.findAllByWorryBoard(worryBoard);
        //s3 삭제
        for(WorryBoardImage worryBoardimage : worryBoardImages) {
            s3Service.deleteFile(s3Service.parseFileName(worryBoardimage.getImgUrl()));
        }
        worryBoardImageRepository.deleteAllByWorryBoard(worryBoard);
    }

    public String uploadFile(MultipartFile multipartFile) {
        return s3Service.uploadImage(multipartFile);
    }
}