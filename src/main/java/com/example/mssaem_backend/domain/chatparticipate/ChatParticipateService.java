package com.example.mssaem_backend.domain.chatparticipate;

import com.example.mssaem_backend.domain.chatmessage.ChatMessage;
import com.example.mssaem_backend.domain.chatmessage.ChatMessageRepository;
import com.example.mssaem_backend.domain.chatparticipate.dto.ChatParticipateResponseDto.ChatParticipateRes;
import com.example.mssaem_backend.domain.chatroom.ChatRoom;
import com.example.mssaem_backend.domain.chatroom.ChatRoomRepository;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.MemberRepository;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.domain.notification.NotificationService;
import com.example.mssaem_backend.domain.notification.NotificationType;
import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.ChatRoomParticipateErrorCode;
import java.util.ArrayList;
import java.util.List;
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
    private final ChatRoomRepository chatRoomRepository;


    @Transactional
    public void insertChatParticipate(String sessionID, ChatRoom chatRoom, String memberName) {
        Member member = memberRepository.findByNickName(memberName);
        ChatParticipate chatParticipate = new ChatParticipate(sessionID, chatRoom, member);

        // 채팅방이 만들어져 있고 해당 채팅방의 두번째 참가자(고민을 올린 사람)일 경우 채팅 시작 알림 전송
        ChatParticipate prevParticipate = chatParticipateRepository.findByChatRoom(chatRoom);
        if (prevParticipate != null) {
            notificationService.createNotification(
                chatRoom.getId(),
                chatRoom.getTitle(),
                NotificationType.CHAT,
                member
            );
        }
        chatParticipateRepository.save(chatParticipate);
    }

    public List<ChatParticipateRes> selectChatRooms(Member member) {
        // 자신이 참여한 모든 채팅방 ID 조회
        List<Long> participateRoomId = chatParticipateRepository.findAllByMemberParticipateRoomId(
            member);

        // 자신이 참여한 모든 채팅방의 상대 조회
        List<ChatParticipate> allParticipateRoom = chatParticipateRepository.findAllParticipateRoom(
            member, participateRoomId);

        if (allParticipateRoom.isEmpty()) {
            throw new BaseException(ChatRoomParticipateErrorCode.EMPTY_CHATPARTICIPATE);
        }

        // 로그인한 멤버의 참여한 채팅방 ID 리스트
        List<Long> participateChatRoomIds = new ArrayList<>();
        for (ChatParticipate chatParticipate : allParticipateRoom) {
            Long id = chatParticipate.getChatRoom().getId();
            participateChatRoomIds.add(id);
        }

        // 각 채팅방에 맞는 고민 게시글 조회
        List<WorryBoard> worryBoardAllByChatRoom = chatRoomRepository.findWorryBoardAllByChatRoom(
            participateChatRoomIds);

        List<ChatMessage> chatMessages = chatMessageRepository.selectByChatRoom(
            participateChatRoomIds);

        List<ChatParticipateRes> result = new ArrayList<>();

        for (int i = 0; i < allParticipateRoom.size(); ++i) {
            ChatParticipate nowChatParticipate = allParticipateRoom.get(i);
            ChatMessage nowChatMessage = null;
            WorryBoard nowWorryBoard = worryBoardAllByChatRoom.get(i);

            for (ChatMessage chm : chatMessages) {
                if (chm.getChatRoom().getId() == nowChatParticipate.getChatRoom().getId()) {
                    nowChatMessage = chm;
                    break;
                }
            }

            result.add(new ChatParticipateRes(nowChatParticipate,
                new MemberSimpleInfo(nowChatParticipate.getMember()),
                nowChatMessage, nowWorryBoard));
        }

        return result;
    }

    @Transactional
    public void deleteAllChatParticipateByChatRoom(ChatRoom chatRoom) {
        chatParticipateRepository.deleteAllByChatRoom(chatRoom);
    }

    public Integer countChatParticipate(Long roomId) {
        return chatParticipateRepository.countByChatRoomId(roomId);
    }

    public Member getPartnerByChatRoomAndMember(Member member, ChatRoom chatRoom){
        return chatParticipateRepository.findByMemberAndChatRoom(member, chatRoom);
    }

}

