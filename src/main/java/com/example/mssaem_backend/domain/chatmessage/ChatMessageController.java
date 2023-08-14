package com.example.mssaem_backend.domain.chatmessage;

import com.example.mssaem_backend.domain.chatmessage.dto.ChatMessageResourceDto.NowChatMessageRes;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/member/allMessages/{roomId}")
    public ResponseEntity<List<NowChatMessageRes>> selectAllChatMessage(
        @CurrentMember Member member, @PathVariable("roomId") Long roomId) {
        return ResponseEntity.ok(chatMessageService.selectAllMessage(member, roomId));
    }
}
