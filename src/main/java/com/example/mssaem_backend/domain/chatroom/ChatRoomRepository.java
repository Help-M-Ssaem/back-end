package com.example.mssaem_backend.domain.chatroom;

import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("select wb from ChatRoom cr join WorryBoard wb on cr.worryBoardId = wb.id where cr.id in (:chatRoomIds)")
    List<WorryBoard> findWorryBoardAllByChatRoom(@Param("chatRoomIds") List<Long> chatRoomIds);
}
