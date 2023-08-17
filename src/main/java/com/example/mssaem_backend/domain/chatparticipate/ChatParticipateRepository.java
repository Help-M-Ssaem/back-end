package com.example.mssaem_backend.domain.chatparticipate;

import com.example.mssaem_backend.domain.chatroom.ChatRoom;
import com.example.mssaem_backend.domain.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatParticipateRepository extends JpaRepository<ChatParticipate, Long> {

    @Query("select cp.chatRoom.id from ChatParticipate cp where cp.member = :member")
    List<Long> findAllByMemberParticipateRoomId(@Param("member") Member member);

    @Query("select cp from ChatParticipate cp join fetch cp.chatRoom join fetch cp.member where cp.member <> :member and cp.chatRoom.id in (:roomIds)")
    List<ChatParticipate> findAllParticipateRoom(@Param("member") Member member,
        @Param("roomIds") List<Long> roomIds);

    ChatParticipate findBySessionId(String sessionId);

    ChatParticipate findByChatRoom(ChatRoom chatRoom);

    Integer countByChatRoomId(Long chatRoomId);

    @Modifying
    void deleteAllByChatRoom(ChatRoom chatRoom);

    @Query("select cp.member from ChatParticipate cp where cp.member <> :member and cp.chatRoom = :chatroom")
    Member findByMemberAndChatRoom(@Param("member") Member member,
        @Param("chatroom") ChatRoom chatroom);
}
