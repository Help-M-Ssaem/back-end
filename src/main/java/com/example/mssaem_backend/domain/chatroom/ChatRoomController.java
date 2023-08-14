package com.example.mssaem_backend.domain.chatroom;

import com.example.mssaem_backend.domain.chatroom.dto.ChatRoomRequestDto.ChatInfo;
import com.example.mssaem_backend.domain.chatroom.dto.ChatRoomResponseDto.ChatRoomRes;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController

public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 채팅방 개설
     */
    @PostMapping("member/chat/rooms/{worryBoardId}")
    public ResponseEntity<ChatRoomRes> createRoom(@PathVariable("worryBoardId") Long worryBoardId) {
        return ResponseEntity.ok(chatRoomService.createRoom(worryBoardId));
    }

    /**
     * Redis에 저장된 채팅방 조회(Test)
     */
    @GetMapping("/redis/rooms")
    public List<ChatRoom> selectChatRoom() {
        return chatRoomService.selectRedisChatRoom();
    }

    /**
     * Redis에 저장한 Enter 조회
     */
    @GetMapping("/redis/enter/{sessionId}")
    public ChatInfo enterChat(@PathVariable("sessionId") String sessionId) {
        return chatRoomService.chatEnter(sessionId);
    }

    /**
     * 채팅방 삭제
     */
    @DeleteMapping("member/chat/rooms/{roomId}")
    public ResponseEntity<String> deleteChatRoom(@PathVariable("roomId") Long roomId) {
        return ResponseEntity.ok(chatRoomService.deleteChatRoom(roomId));
    }

}
