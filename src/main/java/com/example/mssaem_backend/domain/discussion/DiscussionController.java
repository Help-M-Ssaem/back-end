package com.example.mssaem_backend.domain.discussion;

import com.example.mssaem_backend.domain.discussion.dto.DiscussionRequestDto.DiscussionReq;
import com.example.mssaem_backend.domain.discussion.dto.DiscussionResponseDto.DiscussionDetailInfo;
import com.example.mssaem_backend.domain.discussion.dto.DiscussionResponseDto.DiscussionSimpleInfo;
import com.example.mssaem_backend.domain.discussionoption.dto.DiscussionOptionResponseDto.DiscussionOptionLoginInfo;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.ok(discussionService.findHotDiscussionsMore(member, page, size));
    }

    /**
     * 홈 화면 조회 - HOT 토론 2개
     */
    @GetMapping("/discussions/home")
    public ResponseEntity<List<DiscussionSimpleInfo>> findHotDiscussionListForHome(
        @CurrentMember Member member) {
        return ResponseEntity.ok(discussionService.findHotDiscussionsForHome(member));
    }

    /**
     * 토론 검색하기
     */
    @GetMapping("/discussions/search")
    public ResponseEntity<PageResponseDto<List<DiscussionSimpleInfo>>> findDiscussionListByKeyword(
        @CurrentMember Member member, @RequestParam int searchType, @RequestParam String keyword, @RequestParam int page,
        @RequestParam int size) {
        return ResponseEntity.ok(
            discussionService.findDiscussionListByKeyword(member, searchType, keyword, page, size));
    }

    /**
     * 토론글 생성
     */
    @PostMapping("/member/discussion")
    public ResponseEntity<String> createDiscussion(@CurrentMember Member member,
        @RequestPart(value = "image", required = false) List<String> imgUrls,
        @RequestPart(value = "DiscussionReq") DiscussionReq postDiscussionReq) {
        return ResponseEntity.ok(
            discussionService.createDiscussion(member, imgUrls, postDiscussionReq));
    }

    /**
     * 토론글 수정
     */
    @PatchMapping("/member/discussion/{id}")
    public ResponseEntity<String> modifyDiscussion(@CurrentMember Member member,
        @PathVariable Long id,
        @RequestPart(value = "image", required = false) List<String> imgUrls,
        @RequestPart(value = "DiscussionReq") DiscussionReq patchDiscussionReq) {
        return ResponseEntity.ok(
            discussionService.modifyDiscussion(member, id, patchDiscussionReq, imgUrls));
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
    public ResponseEntity<List<DiscussionOptionLoginInfo>> selectDiscussion(
        @CurrentMember Member member,
        @PathVariable Long discussionId,
        @PathVariable Long discussionOptionId
    ) {
        return ResponseEntity.ok(
            discussionService.participateDiscussion(member, discussionId, discussionOptionId));
    }

    /**
     * 토론글 전체조회
     */
    @GetMapping("/discussions")
    public ResponseEntity<PageResponseDto<List<DiscussionSimpleInfo>>> findDiscussions(
        @CurrentMember Member member,
        @RequestParam int page,
        @RequestParam int size) {
        return ResponseEntity.ok(discussionService.findDiscussions(member, page, size));
    }

    /**
     * 토론글 상세조회
     */
    @GetMapping("/discussions/{id}")
    public ResponseEntity<DiscussionDetailInfo> findDiscussion(
        @CurrentMember Member member,
        @PathVariable Long id) {
        return ResponseEntity.ok(discussionService.findDiscussion(member, id));
    }

    /**
     * 멤버별 올린 토론글 조회
     */
    @GetMapping("/discussion/post-list")
    public ResponseEntity<PageResponseDto<List<DiscussionSimpleInfo>>> findDiscussionsById(
        @CurrentMember Member member,
        @RequestParam(required = false) Long memberId, @RequestParam(value = "page") int page,
        @RequestParam(value = "size") int size) {
        return ResponseEntity.ok(
            discussionService.findDiscussionsByMemberId(member, memberId, page, size));
    }
}