package com.example.mssaem_backend.domain.chatmessage;

import static com.example.mssaem_backend.global.config.exception.errorCode.ChatMessageErrorCode.EMPTY_CHATMESSAGE;

import com.example.mssaem_backend.domain.chatmessage.dto.ChatMessageResourceDto.NowChatMessageRes;
import com.example.mssaem_backend.domain.chatroom.ChatRoom;
import com.example.mssaem_backend.domain.chatroom.ChatRoomRepository;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.config.exception.BaseException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public void insertChatMessage(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
    }

    @Transactional
    public void deleteAllChatMessage(ChatRoom ChatRoom) {
        chatMessageRepository.deleteAllByChatRoom(ChatRoom);
    }

    public List<NowChatMessageRes> selectAllMessage(Member member, Long chatRoomId) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();
        List<ChatMessage> chatMessages = chatMessageRepository.selectAllChatMessage(chatRoom);
        if(chatMessages.isEmpty()){
            throw new BaseException(EMPTY_CHATMESSAGE);
        }
        return chatMessages.stream()
            .map(ch -> {
                int sendWho = 2;
                if (member.getNickName().equals(ch.getSender())) {
                    sendWho = 1;
                }
                return new NowChatMessageRes(ch, sendWho);
            }).collect(Collectors.toList());
    }
}
