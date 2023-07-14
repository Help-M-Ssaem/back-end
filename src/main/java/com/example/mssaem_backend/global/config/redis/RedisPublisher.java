package com.example.mssaem_backend.global.config.redis;

import com.example.mssaem_backend.domain.chatmessage.dto.ChatMessageResourceDto.ChatMessageRes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisPublisher {
  private final RedisTemplate<String, Object> redisTemplate;
  public void publish(ChannelTopic topic, ChatMessageRes message) {
    redisTemplate.convertAndSend(topic.getTopic(), message);
  }
}
