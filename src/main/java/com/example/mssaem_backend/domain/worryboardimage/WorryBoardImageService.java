package com.example.mssaem_backend.domain.worryboardimage;

import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import com.example.mssaem_backend.global.s3.S3Service;
import com.example.mssaem_backend.global.s3.dto.S3Result;
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

    //worryBoardImage 저장
    public void uploadWorryImage(WorryBoard worryBoard, List<MultipartFile> multipartFiles) {
        //s3 저장 후 url리스트 가져오기
        List<S3Result> s3ResultList = s3Service.uploadFile(multipartFiles);

        //받은 url을 worryBoardImage로 저장
        if (!s3ResultList.isEmpty()) {
            for (S3Result s3Result : s3ResultList) {
                worryBoardImageRepository.save(WorryBoardImage.builder()
                    .worryBoard(worryBoard)
                    .imgUrl(s3Result.getImgUrl())
                    .build());
            }
        }
    }

    //worryBoardImage 삭제
    public void deleteWorryImage(WorryBoard worryBoard) {
        worryBoardImageRepository.findAllByWorryBoard(worryBoard);
    }
}