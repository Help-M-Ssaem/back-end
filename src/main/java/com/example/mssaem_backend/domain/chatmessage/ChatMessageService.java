package com.example.mssaem_backend.domain.chatmessage;

import com.example.mssaem_backend.domain.chatroom.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;

  public void insertChatMessage(ChatMessage chatMessage) {
    chatMessageRepository.save(chatMessage);
  }

  @Transactional
  public void deleteAllChatMessage(ChatRoom ChatRoom){
    chatMessageRepository.deleteAllByChatRoom(ChatRoom);
  }
}
