package com.example.mssaem_backend.domain.discussion;

import com.example.mssaem_backend.domain.discussion.dto.DiscussionRequestDto.DiscussionReq;
import com.example.mssaem_backend.domain.discussion.dto.DiscussionResponseDto.DiscussionSimpleInfo;
import com.example.mssaem_backend.domain.discussionoption.DiscussionOption;
import com.example.mssaem_backend.domain.discussionoption.dto.DiscussionOptionResponseDto.DiscussionOptionSelectedInfo;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.search.dto.SearchRequestDto.SearchReq;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
     * 토론 검색하기
     */
    @GetMapping("/discussions/search")
    public ResponseEntity<PageResponseDto<List<DiscussionSimpleInfo>>> findDiscussionListByKeyword(
        @CurrentMember Member member, @RequestBody SearchReq searchReq, @RequestParam int page,
        @RequestParam int size) {
        return ResponseEntity.ok(
            discussionService.findDiscussionListByKeyword(member, searchReq, page, size));
    }

    /**
     * 토론글 생성
     */
    @PostMapping("/member/discussion")
    public ResponseEntity<String> createDiscussion(@CurrentMember Member member,
        @RequestPart(value = "image", required = false) List<MultipartFile> multipartFiles,
        @RequestPart(value = "DiscussionReq") DiscussionReq postDiscussionReq) {
        return ResponseEntity.ok(
            discussionService.createDiscussion(member, multipartFiles, postDiscussionReq));
    }

    /**
     * 토론글 수정
     */
    @PatchMapping("/member/discussion/{id}")
    public ResponseEntity<String> modifyDiscussion(@CurrentMember Member member,
        @PathVariable Long id,
        @RequestPart(value = "image", required = false) List<MultipartFile> multipartFiles,
        @RequestPart(value = "DiscussionReq") DiscussionReq patchDiscussionReq) {
        return ResponseEntity.ok(
            discussionService.modifyDiscussion(member, id, patchDiscussionReq, multipartFiles));
    }

    /**
     * 토론글 삭제
     */
    @DeleteMapping("/member/discussion/{id}")
    public ResponseEntity<String> deleteDiscussion(@CurrentMember Member member,
        @PathVariable Long id) {
        return ResponseEntity.ok(
            discussionService.deleteDiscussion(member, id));
    }

    /**
     * 토론글 참여하기
     */
    @PostMapping("/member/discussions/{discussionId}/discussion-options/{discussionOptionId}")
    public ResponseEntity<List<DiscussionOptionSelectedInfo>> selectDiscussion(@CurrentMember Member member,
        @PathVariable Long discussionId,
        @PathVariable Long discussionOptionId
    ) {
        return ResponseEntity.ok(
            discussionService.participateDiscussion(member, discussionId, discussionOptionId));
    }
}