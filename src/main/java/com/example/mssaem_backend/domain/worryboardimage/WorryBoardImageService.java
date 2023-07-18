package com.example.mssaem_backend.domain.worryboardimage;

import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WorryBoardImageService {

    private final WorryBoardImageRepository worryBoardImageRepository;

    //해당 고민글 이미지 url 리스트 가져오기
    public List<String> getImgUrls(WorryBoard worryBoard) {
        List<WorryBoardImage> worryBoardImages = worryBoardImageRepository.findAllByWorryBoard(
            worryBoard);

        if (worryBoardImages.isEmpty()) {
            return Collections.singletonList("default");
        }
        return worryBoardImages.stream()
            .map(WorryBoardImage::getImgUrl)
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
}