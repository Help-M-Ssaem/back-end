package com.example.mssaem_backend.domain.discussion;

import com.example.mssaem_backend.domain.discussion.dto.DiscussionRequestDto.PostDiscussionReq;
import com.example.mssaem_backend.domain.discussion.dto.DiscussionResponseDto.DiscussionSimpleInfo;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class DiscussionController {

    private final DiscussionService discussionService;

    /**
     * HOT 토론 더보기
     */
    @GetMapping("/discussions/hot")
    public ResponseEntity<PageResponseDto<List<DiscussionSimpleInfo>>> findHotDiscussionList(
        @CurrentMember Member member, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(discussionService.findHotDiscussionList(member, page, size));
    }

    /**
     * 홈 화면 조회 - HOT 토론 2개
     */
    @GetMapping("/discussions/home")
    public ResponseEntity<List<DiscussionSimpleInfo>> findHotDiscussionListForHome(
        @CurrentMember Member member) {
        return ResponseEntity.ok(discussionService.findHotDiscussionListForHome(member));
    }

    /**
     * 토론글 생성
     */
    @PostMapping("/member/discussion")
    public ResponseEntity<String> createDiscussion(
        @CurrentMember Member member,
        @RequestPart(value="image") List<MultipartFile> multipartFiles,
        @RequestPart(value="postDiscussionReq") PostDiscussionReq postDiscussionReq) {
        return ResponseEntity.ok(
            discussionService.createDiscussion(member, multipartFiles, postDiscussionReq));
    }
}
