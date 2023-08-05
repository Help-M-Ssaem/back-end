package com.example.mssaem_backend.domain.chatparticipate;

import com.example.mssaem_backend.domain.chatmessage.ChatMessageRepository;
import com.example.mssaem_backend.domain.chatparticipate.dto.ChatParticipateResponseDto.ChatParticipateRes;
import com.example.mssaem_backend.domain.chatroom.ChatRoom;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.MemberRepository;
import com.example.mssaem_backend.domain.notification.NotificationService;
import com.example.mssaem_backend.domain.notification.TypeEnum;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.ChatRoomParticipateErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class ChatParticipateService {

    private final ChatParticipateRepository chatParticipateRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;
  private final ChatMessageRepository chatMessageRepository;


    @Transactional
    public void insertChatParticipate(String sessionID, ChatRoom chatRoom, String memberName) {
        Member member = memberRepository.findByNickName(memberName);
        ChatParticipate chatParticipate = new ChatParticipate(sessionID, chatRoom, member);

        // 채팅방이 만들어져 있고 해당 채팅방의 두번째 참가자(고민을 올린 사람)일 경우 채팅 시작 알림 전송
        ChatParticipate prevParticipate = chatParticipateRepository.findByChatRoom(chatRoom);
        System.out.println(prevParticipate +"존재함");
        if (prevParticipate != null) {
            notificationService.createChatNotification(
                chatRoom.getId(),
                chatRoom.getTitle(),
                TypeEnum.CHAT,
                prevParticipate.getMember(),
                member
            );
        }
        chatParticipateRepository.save(chatParticipate);
    }

  public List<ChatParticipateRes> selectChatRooms(Member member) {
    List<ChatParticipate> result = chatParticipateRepository.findAllByMember(member);

    if (result.isEmpty()) {
      throw new BaseException(ChatRoomParticipateErrorCode.EMPTY_CHATPARTICIPATE);
    }

    return result.stream()
        .map(r -> new ChatParticipateRes(r,
            new MemberSimpleInfo(r.getMember(), r.getMember().getBadgeName()),
            chatMessageRepository.selectByChatRoom(r.getChatRoom()))).collect(Collectors.toList());
  }

  @Transactional
  public void deleteChatParticipate(String sessionId) {
    ChatParticipate chatParticipate = chatParticipateRepository.findBySessionId(sessionId);
    chatParticipateRepository.delete(chatParticipate);
  }

}
