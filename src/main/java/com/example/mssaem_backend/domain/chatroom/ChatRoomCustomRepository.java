package com.example.mssaem_backend.domain.chatroom;

import com.example.mssaem_backend.domain.chatroom.dto.ChatRoomRequestDto.ChatInfo;
import com.example.mssaem_backend.domain.chatroom.dto.ChatRoomRequestDto.ChatRoomInfo;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ChatRoomCustomRepository implements Serializable {

  private static final String CHAT_ROOMS = "CHAT_ROOM";
  public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장

  @Resource(name = "redisTemplate")
  private HashOperations<String, Long, ChatRoom> opsHashChatRoom;
  // 채팅방의 대화 메시지를 발행하기 위한 redis topic 정보. 서버별로 채팅방에 매치되는 topic정보를 Map에 넣어 roomId로 찾을수 있도록 한다.
  @Resource(name = "redisTemplate")
  private HashOperations<String, String, ChatInfo> hashOpsEnterInfo;

  private final ChatRoomRepository chatRoomRepository;

  public List<ChatRoom> findAllRoom() {
    return opsHashChatRoom.values(CHAT_ROOMS);
  }

  public ChatRoom findRoomById(Long id) {
    return opsHashChatRoom.get(CHAT_ROOMS, id);
  }

  /**
   * 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
   */
  public ChatRoom createChatRoom(ChatRoomInfo chatRoomInfo) {
    ChatRoom chatRoom = ChatRoom.create(chatRoomInfo.getTitle(), chatRoomInfo.getWorryBoardId());
    chatRoomRepository.save(chatRoom);
    opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getId(), chatRoom);
    return chatRoom;
  }

  // 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
  public void setUserEnterInfo(String sessionId, ChatInfo chatInfo) {
    hashOpsEnterInfo.put(ENTER_INFO, sessionId, chatInfo);
    System.out.println(3);
  }

  public void setRoomEnterInfo(String sessionId, Long roomId) {
    ChatInfo chatInfo = hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    chatInfo.setChatRoomId(roomId);
    hashOpsEnterInfo.put(ENTER_INFO, sessionId, chatInfo);
  }

  // 유저 세션으로 입장해 있는 채팅방 ID 조회
  public ChatInfo getUserEnterRoomId(String sessionId) {
    return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
  }

  // 유저 세션정보와 맵핑된 채팅방ID 삭제
  public void removeUserEnterInfo(String sessionId) {
    hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
  }

}