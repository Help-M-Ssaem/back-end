package com.example.mssaem_backend.domain.worryboard;

import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardRequestDto.GetWorriesReq;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardRequestDto.PatchWorrySolvedReq;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardResponseDto.GetWorriesRes;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardResponseDto.GetWorryRes;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardResponseDto.PatchWorrySolvedRes;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class WorryBoardController {

    private final WorryBoardService worryBoardService;

    //고민글 조회 (해결 X)
    @GetMapping("/worry-board/waiting")
    public ResponseEntity<PageResponseDto<List<GetWorriesRes>>> findWorriesWaiting(
        @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(worryBoardService.findWorriesByState(false, page, size));
    }

    //고민글 조회 (해결 O)
    @GetMapping("/worry-board/solved")
    public ResponseEntity<PageResponseDto<List<GetWorriesRes>>> findWorriesSolved(
        @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(worryBoardService.findWorriesByState(true, page, size));
    }

    //고민글 상세 조회
    @GetMapping("/worry-board/{id}")
    public ResponseEntity<GetWorryRes> findWorryById(@CurrentMember Member viewer,
        @PathVariable Long id) {
        return ResponseEntity.ok(worryBoardService.findWorryById(viewer, id));
    }

    //특정 멤버별 올린 고민글 조회
    @GetMapping("/worry-board/post-list")
    public ResponseEntity<PageResponseDto<List<GetWorriesRes>>> findWorriesByMemberId(
        @RequestParam Long memberId, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(worryBoardService.findWorriesByMemberId(memberId, page, size));
    }

    //특정 멤버별 해결한 고민글 조회
    @GetMapping("/worry-board/solve-list")
    public ResponseEntity<PageResponseDto<List<GetWorriesRes>>> findSolveWorriesByMemberId(
        @RequestParam Long memberId, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(
            worryBoardService.findSolveWorriesByMemberId(memberId, page, size));
    }

    //고민 게시판(해결 X) - mbti별 고민글 조회
    @PostMapping("worry-board/waiting/filter")
    public ResponseEntity<PageResponseDto<List<GetWorriesRes>>> findWaitingWorriesByMbti(
        @RequestBody GetWorriesReq getWorriesReq, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(
            worryBoardService.findWorriesByMbti(getWorriesReq, false, page, size));
    }

    //고민 게시판(해결 O) - mbti별 고민글 조회
    @PostMapping("worry-board/solved/filter")
    public ResponseEntity<PageResponseDto<List<GetWorriesRes>>> findSolvedWorriesByMbti(
        @RequestBody GetWorriesReq getWorriesReq, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(
            worryBoardService.findWorriesByMbti(getWorriesReq, true, page, size));
    }

    //고민 해결 완료 처리
    @PostMapping("/member/worry-board/solved")
    public ResponseEntity<PatchWorrySolvedRes> solvedWorryBoard(@CurrentMember Member currentMember, @RequestBody
    PatchWorrySolvedReq patchWorryReq) {
        return ResponseEntity.ok(worryBoardService.solvedWorryBoard(patchWorryReq));
    }
}