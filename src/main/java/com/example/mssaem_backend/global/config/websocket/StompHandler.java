package com.example.mssaem_backend.global.config.websocket;

import static com.example.mssaem_backend.global.config.exception.errorCode.ChatRoomParticipateErrorCode.FULL_PARTICIPATE;

import com.example.mssaem_backend.domain.chat.ChatService;
import com.example.mssaem_backend.domain.chatmessage.ChatMessage;
import com.example.mssaem_backend.domain.chatmessage.ChatMessage.MessageType;
import com.example.mssaem_backend.domain.chatparticipate.ChatParticipateService;
import com.example.mssaem_backend.domain.chatroom.ChatRoom;
import com.example.mssaem_backend.domain.chatroom.ChatRoomCustomRepository;
import com.example.mssaem_backend.domain.chatroom.ChatRoomRepository;
import com.example.mssaem_backend.domain.chatroom.dto.ChatRoomRequestDto.ChatInfo;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.MemberRepository;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.security.jwt.JwtTokenProvider;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatService chatService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomCustomRepository chatRoomCustomRepository;
    private final MemberRepository memberRepository;
    private final ChatParticipateService chatParticipateService;

    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            // 멤버 검사
            String token = accessor.getFirstNativeHeader("token");
            String memberIdByToken = jwtTokenProvider.getMemberIdByToken(token);
            Member member = memberRepository.findById(Long.valueOf(memberIdByToken)).orElseThrow();

            // 멤버 저장 (Redis)
            ChatInfo chatInfo = new ChatInfo();
            chatInfo.setSender(member.getNickName());

            if (chatRoomCustomRepository.getUserEnterRoomId(sessionId) != null) {
                throw new BaseException(FULL_PARTICIPATE);
            }

            chatRoomCustomRepository.setUserEnterInfo(sessionId, chatInfo);
            jwtTokenProvider.validateToken(token);
            log.info("CONNECT {}", token);
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            String sessionId = (String) message.getHeaders().get("simpSessionId");

            //roomId 뽑아오기
            String roomId = chatService.getRoomId(
                Optional.ofNullable((String) message.getHeaders().get("simpDestination"))
                    .orElse("InvalidRoom"));

            if (chatParticipateService.countChatParticipate(Long.valueOf(roomId)) >= 2) {
                throw new BaseException(FULL_PARTICIPATE);
            }
            //roomId 다시 저장 (Redis)
            chatRoomCustomRepository.setRoomEnterInfo(sessionId, Long.valueOf(roomId));

            ChatInfo chatInfo = chatRoomCustomRepository.getUserEnterRoomId(sessionId);

            ChatRoom chatRoom = chatRoomRepository.findById(Long.valueOf(roomId)).orElseThrow();
            ChatMessage chatMessage = new ChatMessage(MessageType.ENTER, chatInfo.getSender(),
                chatRoom);
            chatParticipateService.insertChatParticipate(sessionId, chatRoom, chatInfo.getSender());

            chatService.sendChatMessage(chatMessage);
            log.info("SUBSCRIBED {}, {}", sessionId, roomId);
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            String sessionId = (String) message.getHeaders().get("simpSessionId");

            ChatInfo chatInfo = chatRoomCustomRepository.getUserEnterRoomId(sessionId);

            if (chatInfo == null) {
                return message;
            }

            if (chatInfo.getChatRoomId() == null) {
                return message;
            }

            ChatRoom chatRoom = chatRoomRepository.findById(chatInfo.getChatRoomId()).orElseThrow();

            ChatMessage chatMessage = new ChatMessage(MessageType.QUIT, chatInfo.getSender(),
                chatRoom);
            chatService.sendChatMessage(chatMessage);

            // 채팅 참여 기록 삭제
            chatRoomCustomRepository.removeUserEnterInfo(sessionId);
            log.info("DISCONNECTED {}, {}", sessionId, chatInfo.getChatRoomId());
        }
        return message;
    }
}
