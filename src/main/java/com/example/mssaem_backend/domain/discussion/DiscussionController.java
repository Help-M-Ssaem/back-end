package com.example.mssaem_backend.domain.discussion;

import com.example.mssaem_backend.domain.discussion.dto.DiscussionResponseDto.DiscussionSimpleInfo;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class DiscussionController {

    private final DiscussionService discussionService;

    @GetMapping("/discussions/hot")
    public ResponseEntity<PageResponseDto<List<DiscussionSimpleInfo>>> findHotDiscussionList(
        @CurrentMember Member member, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(discussionService.findHotDiscussionList(member, page, size));
    }
}
