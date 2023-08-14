package com.example.mssaem_backend.domain.chatroom;

import static com.example.mssaem_backend.global.config.exception.errorCode.WorryBoardErrorCode.EMPTY_WORRY_BOARD;

import com.example.mssaem_backend.domain.chatmessage.ChatMessageService;
import com.example.mssaem_backend.domain.chatroom.dto.ChatRoomRequestDto.ChatInfo;
import com.example.mssaem_backend.domain.chatroom.dto.ChatRoomResponseDto.ChatRoomRes;
import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import com.example.mssaem_backend.domain.worryboard.WorryBoardRepository;
import com.example.mssaem_backend.global.config.exception.BaseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomCustomRepository chatRoomCustomRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageService chatMessageService;
    private final WorryBoardRepository worryBoardRepository;

    public ChatRoomRes createRoom(Long worryBoardId) {
        ChatRoom chatRoom = chatRoomCustomRepository.createChatRoom(worryBoardId);
        return new ChatRoomRes(chatRoom);
    }

    /**
     * Redis에 저장되어 있는 chatRoom 조회
     */
    public List<ChatRoom> selectRedisChatRoom() {
        return chatRoomCustomRepository.findAllRoom();
    }

    public ChatInfo chatEnter(String sessionId) {
        return chatRoomCustomRepository.getUserEnterRoomId(sessionId);
    }

    @Transactional
    public String deleteChatRoom(Long chatRoomId) {
        // Redis에서 채팅방 삭제
        chatRoomCustomRepository.removeChatRoomInfo(chatRoomId);
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();
        // 모든 채팅 메시지 삭제
        chatMessageService.deleteAllChatMessage(chatRoom);
        // 채팅방 삭제
        chatRoomRepository.delete(chatRoom);
        return "채팅방 삭제 완료";
    }

    public Boolean selectChatRoomStateByWorryBoard(Long worryBoardId) {
        WorryBoard worryBoard = worryBoardRepository.findById(worryBoardId)
            .orElseThrow(() -> new BaseException(EMPTY_WORRY_BOARD));
        return chatRoomRepository.existsByWorryBoardId(worryBoardId);
    }
}
