package com.example.mssaem_backend.domain.chatmessage;

import com.example.mssaem_backend.domain.chatroom.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
  @Query("select chm from ChatMessage chm where (chm.chatRoom.id, chm.createdAt) in "
      + "(select chm2.chatRoom.id, MAX(chm2.createdAt) from ChatMessage chm2"
      + " where chm2.chatRoom.id in :chatRoomIds group by chm2.chatRoom.id)")
  List<ChatMessage> selectByChatRoom(@Param("chatRoomIds") List<Long> chatRoomIds);

  @Modifying
  void deleteAllByChatRoom(ChatRoom chatRoom);

  @Query("select chm from ChatMessage chm join fetch chm.chatRoom where chm.chatRoom = :chatRoom")
  List<ChatMessage> selectAllChatMessage(@Param("chatRoom") ChatRoom chatRoom);
}
