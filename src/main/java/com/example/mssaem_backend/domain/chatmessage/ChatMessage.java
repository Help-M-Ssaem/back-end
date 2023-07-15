package com.example.mssaem_backend.domain.chatmessage;

import com.example.mssaem_backend.domain.chatroom.ChatRoom;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatMessage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private MessageType type; // 메시지 타입

    private String sender; // 메시지 보낸사람

    private String message; // 메시지

    @ColumnDefault("false")
    private boolean state; //true : 확인, false : 확인 안함

    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom; // 방번호

    public static ChatMessage createChatMessage(ChatRoom chatRoom, String sender, String message, MessageType type) {
        ChatMessage chatMessage= ChatMessage.builder()
            .chatRoom(chatRoom)
            .sender(sender)
            .message(message)
            .type(type)
            .build();
        return chatMessage;
    }

    public void setSender(String sender){
        this.sender=sender;
    }

    public void setMessage(String message){
        this.message=message;
    }

    // 메시지 타입 : 입장, 퇴장, 채팅
    public enum MessageType {
        ENTER, QUIT, TALK
    }


}
