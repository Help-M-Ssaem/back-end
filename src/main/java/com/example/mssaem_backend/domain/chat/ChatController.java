package com.example.mssaem_backend.domain.chat;

import static com.example.mssaem_backend.global.config.exception.errorCode.MemberErrorCode.EMPTY_MEMBER;

import com.example.mssaem_backend.domain.chatmessage.ChatMessage;
import com.example.mssaem_backend.domain.chatmessage.ChatMessageService;
import com.example.mssaem_backend.domain.chatmessage.dto.ChatMessageResourceDto.ChatMessageRes;
import com.example.mssaem_backend.domain.chatparticipate.ChatParticipateService;
import com.example.mssaem_backend.domain.chatroom.ChatRoom;
import com.example.mssaem_backend.domain.chatroom.ChatRoomCustomRepository;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.MemberRepository;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.redis.RedisPublisher;
import com.example.mssaem_backend.global.config.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatController {
  private final RedisPublisher redisPublisher;
  private final ChatRoomCustomRepository chatService;
  private final ChatParticipateService chatParticipateService;
  private final ChatMessageService chatMessageService;
  private final JwtTokenProvider jwtTokenProvider;
  private final MemberRepository memberRepository;


  /**
   * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
   */
  @MessageMapping("/chat/message")
  public void message(ChatMessageDto request) {
    String memberIdByToken = jwtTokenProvider.getMemberIdByToken(request.getAccessToken());
    Member member = memberRepository.findById(Long.parseLong(memberIdByToken)).orElseThrow(() -> new BaseException(EMPTY_MEMBER));
    // 로그인 회원 정보로 대화명 설정
    ChatRoom chatRoom=chatService.findRoomById(request.getRoomId());
    ChatMessage message=ChatMessage.createChatMessage(chatRoom, member.getNickName(), request.getMessage(), request.getType());
    ChatMessageRes chatMessageRes = new ChatMessageRes(message);
    // 채팅방 입장시에는 대화명과 메시지를 자동으로 세팅한다.
    if (ChatMessage.MessageType.ENTER.equals(chatMessageRes.getType())) {
      chatService.enterChatRoom(request.getRoomId());
      chatMessageRes.setMessage(chatMessageRes.getSender() + "님이 입장하셨습니다.");
      //참여자 저장
      chatParticipateService.insertChatParticipate(chatRoom,member.getId());
    }
    // Websocket에 발행된 메시지를 redis로 발행(publish)
    redisPublisher.publish(chatService.getTopic(request.getRoomId()), chatMessageRes);
    chatMessageService.insertChatMessage(message);
  }

}
