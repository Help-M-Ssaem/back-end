package com.example.mssaem_backend.domain.worryboardimage;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class WorryBoardImageController {

    private final WorryBoardImageService worryBoardImageService;

    @PostMapping("/member/worry-boards/files")
    public ResponseEntity<String> uploadFiles(
        @RequestPart(value = "image", required = false) MultipartFile multipartFile)
        throws IOException {
        return ResponseEntity.ok(worryBoardImageService.uploadFile(multipartFile));
    }
}
