package com.example.mssaem_backend.domain.worryboard;

import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.search.dto.SearchRequestDto.SearchReq;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardRequestDto.PatchWorryReq;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardRequestDto.PatchWorrySolvedReq;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardRequestDto.PostWorryReq;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardResponseDto.GetWorriesRes;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardResponseDto.GetWorryBoardId;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardResponseDto.GetWorryRes;
import com.example.mssaem_backend.domain.worryboard.dto.WorryBoardResponseDto.PatchWorrySolvedRes;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class WorryBoardController {

    private final WorryBoardService worryBoardService;

    //고민글 조회 (해결 X)
    @GetMapping("/worry-board/waiting")
    public ResponseEntity<PageResponseDto<List<GetWorriesRes>>> findWorriesWaiting(
        @RequestParam(required = false) Long worryBoardId, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(worryBoardService.findWorriesBySolved(false, worryBoardId, page, size));
    }

    //고민글 조회 (해결 O)
    @GetMapping("/worry-board/solved")
    public ResponseEntity<PageResponseDto<List<GetWorriesRes>>> findWorriesSolved(
        @RequestParam(required = false) Long worryBoardId, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(worryBoardService.findWorriesBySolved(true, worryBoardId, page, size));
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
    @GetMapping("worry-board/waiting/filter")
    public ResponseEntity<PageResponseDto<List<GetWorriesRes>>> findWaitingWorriesByMbti(
        @RequestParam String strFromMbti, @RequestParam String strToMbti,
        @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(
            worryBoardService.findWorriesByMbti(false, strFromMbti, strToMbti, page, size));
    }

    //고민 게시판(해결 O) - mbti별 고민글 조회
    @GetMapping("worry-board/solved/filter")
    public ResponseEntity<PageResponseDto<List<GetWorriesRes>>> findSolvedWorriesByMbti(
        @RequestParam String strFromMbti, @RequestParam String strToMbti,
        @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(
            worryBoardService.findWorriesByMbti(true, strFromMbti, strToMbti, page, size));
    }

    //홈 화면 조회 - 고민글 6개 조회 (해결 X, 최신순)
    @GetMapping("/worry-board/home")
    public ResponseEntity<List<GetWorriesRes>> findWaitingWorriesForHome() {
        return ResponseEntity.ok(worryBoardService.findWorriesForHome());
    }

    //고민 해결 완료 처리
    @PatchMapping("/member/worry-board/{id}/solved")
    public ResponseEntity<PatchWorrySolvedRes> solveWorryBoard(@CurrentMember Member currentMember,
        @PathVariable Long id,
        @RequestBody PatchWorrySolvedReq patchWorryReq) {
        return ResponseEntity.ok(
            worryBoardService.solveWorryBoard(currentMember, id, patchWorryReq));
    }

    //고민글 생성
    @PostMapping("/member/worry-board")
    public ResponseEntity<GetWorryBoardId> createWorryBoard(@CurrentMember Member currentMember,
        @RequestPart(value = "postWorryReq") PostWorryReq postWorryReq,
        @RequestPart(value = "image", required = false) List<String> imgUrls) {
        return ResponseEntity.ok(
            worryBoardService.createWorryBoard(currentMember, postWorryReq, imgUrls));
    }

    //고민글 수정
    @PatchMapping("/member/worry-board/{id}")
    public ResponseEntity<String> modifyWorryBoard(@CurrentMember Member currentMember,
        @PathVariable Long id,
        @RequestPart(value = "patchWorryReq") PatchWorryReq patchWorryReq,
        @RequestPart(value = "image", required = false) List<String> imgUrls) {
        return ResponseEntity.ok(
            worryBoardService.modifyWorryBoard(currentMember, id, patchWorryReq, imgUrls));
    }

    //고민글 삭제
    @DeleteMapping("/member/worry-board/{id}")
    public ResponseEntity<String> deleteWorryBoard(@CurrentMember Member currentMember,
        @PathVariable Long id) {
        return ResponseEntity.ok(
            worryBoardService.deleteWorryBoard(currentMember, id));
    }

    /**
     * 해결한 고민글 검색하기
     */
    @GetMapping("/worry-board/solved/search")
    public ResponseEntity<PageResponseDto<List<GetWorriesRes>>> findSolvedWorriesByKeywordAndMbti(
        @RequestBody SearchReq searchReq,
        @RequestParam String strFromMbti,
        @RequestParam String strToMbti,
        @RequestParam int page,
        @RequestParam int size) {
        return ResponseEntity.ok(
            worryBoardService.findSolvedWorriesByKeywordAndMbti(searchReq, strFromMbti, strToMbti,
                page, size));
    }

    /**
     * 해결 안 된 고민글 전체 검색하기
     */
    @GetMapping("/worry-board/waiting/search")
    public ResponseEntity<PageResponseDto<List<GetWorriesRes>>> findWaitingWorriesByKeywordAndMbti(
        @RequestBody SearchReq searchReq,
        @RequestParam String strFromMbti,
        @RequestParam String strToMbti,
        @RequestParam int page,
        @RequestParam int size) {
        return ResponseEntity.ok(
            worryBoardService.findWaitingWorriesByKeywordAndMbti(searchReq, strFromMbti, strToMbti,
                page, size));
    }


}