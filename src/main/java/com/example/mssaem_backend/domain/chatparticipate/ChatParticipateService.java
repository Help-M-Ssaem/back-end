package com.example.mssaem_backend.domain.chatparticipate;

import com.example.mssaem_backend.domain.chatmessage.ChatMessageRepository;
import com.example.mssaem_backend.domain.chatparticipate.dto.ChatParticipateResponseDto.ChatParticipateRes;
import com.example.mssaem_backend.domain.chatroom.ChatRoom;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.MemberRepository;
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
  private final ChatMessageRepository chatMessageRepository;
  private final MemberRepository memberRepository;

  public void insertChatParticipate(String sessionID, ChatRoom chatRoom, String memberName) {
    Member member = memberRepository.findByNickName(memberName);
    ChatParticipate chatParticipate = new ChatParticipate(sessionID, chatRoom, member);
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
