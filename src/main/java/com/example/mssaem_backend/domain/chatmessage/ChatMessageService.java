package com.example.mssaem_backend.domain.chatmessage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;

  public void insertChatMessage(ChatMessage chatMessage) {
    chatMessageRepository.save(chatMessage);
  }
}
