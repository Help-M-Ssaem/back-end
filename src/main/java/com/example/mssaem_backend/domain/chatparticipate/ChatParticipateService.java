package com.example.mssaem_backend.domain.chatparticipate;

import static com.example.mssaem_backend.global.config.exception.errorCode.MemberErrorCode.EMPTY_MEMBER;

import com.example.mssaem_backend.domain.chatroom.ChatRoom;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.MemberRepository;
import com.example.mssaem_backend.global.config.exception.BaseException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatParticipateService {
  private final ChatParticipateRepository chatParticipateRepository;
  private final MemberRepository memberRepository;

  public void insertChatParticipate(ChatRoom chatRoom, Long memberId){
    Member member = memberRepository.findById(memberId).orElseThrow(()->new BaseException(EMPTY_MEMBER));
    ChatParticipate chatParticipate = new ChatParticipate(chatRoom, member);
    chatParticipateRepository.save(chatParticipate);
  }
}
