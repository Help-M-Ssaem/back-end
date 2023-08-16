package com.example.mssaem_backend.domain.chatparticipate;

import com.example.mssaem_backend.domain.chatparticipate.dto.ChatParticipateResponseDto.ChatParticipateRes;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatParticipateController {

    private final ChatParticipateService chatParticipateService;

    /**
     * 현재 로그인 한 member의 채팅방 모두 조회
     */
    @GetMapping("/member/chatRooms")
    public ResponseEntity<List<ChatParticipateRes>> selectChatRooms(@CurrentMember Member member) {
        return ResponseEntity.ok(chatParticipateService.selectChatRooms(member));
    }

}
