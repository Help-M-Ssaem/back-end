package com.example.mssaem_backend.domain.chat;

import com.example.mssaem_backend.domain.chatmessage.ChatMessage;
import com.example.mssaem_backend.domain.chatmessage.ChatMessage.MessageType;
import com.example.mssaem_backend.domain.chatmessage.dto.ChatMessageResourceDto.ChatMessageRes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatService {

  private final ChannelTopic channelTopic;
  private final RedisTemplate redisTemplate;

  /**
   * destination정보에서 roomId 추출
   */
  public String getRoomId(String destination) {
    int lastIndex = destination.lastIndexOf('/');
    if (lastIndex != -1) {
      return destination.substring(lastIndex + 1);
    } else {
      return "";
    }
  }

  public void sendChatMessage(ChatMessage chatMessage) {
    if (MessageType.ENTER.equals(chatMessage.getType())) {
      chatMessage.setMessage(chatMessage.getSender() + "님이 방에 입장했습니다.");
      chatMessage.setSender("[알림]");
    } else if (MessageType.QUIT.equals(chatMessage.getType())) {
      chatMessage.setMessage(chatMessage.getSender() + "님이 방에서 나갔습니다.");
      chatMessage.setSender("[알림]");
    }
    redisTemplate.convertAndSend(channelTopic.getTopic(), new ChatMessageRes(chatMessage));
  }
}
