package com.example.mssaem_backend.domain.boardimage;

import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class BoardImageController {

    private final BoardImageService boardImageService;


    //게시글 생성 시 파일 업로드 : 파일 하나 받아서 S3에 저장 후 url 반환
    @PostMapping("/member/boards/files")
    public ResponseEntity<String> uploadFiles(@CurrentMember Member member,
        @RequestPart(value = "image", required = false) MultipartFile multipartFile)
        throws IOException {
        return ResponseEntity.ok(boardImageService.uploadFile(multipartFile));
    }
}
