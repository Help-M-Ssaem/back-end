package com.example.mssaem_backend.domain.chatmessage;

import com.example.mssaem_backend.domain.chatroom.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
  @Query("select chm from ChatMessage chm where chm.chatRoom = :chatRoom order by chm.createdAt desc limit 1")
  ChatMessage selectByChatRoom(@Param("chatRoom") ChatRoom chatRoom);

  @Modifying
  void deleteAllByChatRoom(ChatRoom chatRoom);

  @Query("select chm from ChatMessage chm join fetch chm.chatRoom where chm.chatRoom = :chatRoom order by chm.createdAt desc")
  List<ChatMessage> selectAllChatMessage(@Param("chatRoom") ChatRoom chatRoom);
}
