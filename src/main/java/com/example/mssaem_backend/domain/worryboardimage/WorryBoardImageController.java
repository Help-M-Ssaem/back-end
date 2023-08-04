package com.example.mssaem_backend.domain.worryboardimage;

import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
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
    public ResponseEntity<String> uploadFiles(@CurrentMember Member member,
        @RequestPart(value="image", required = false)MultipartFile multipartFile) {
        return ResponseEntity.ok(worryBoardImageService.uploadFile(multipartFile));
    }
}
