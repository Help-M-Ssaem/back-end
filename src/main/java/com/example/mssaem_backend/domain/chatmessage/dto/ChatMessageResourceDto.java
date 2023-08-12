package com.example.mssaem_backend.domain.chatmessage.dto;


import com.example.mssaem_backend.domain.chatmessage.ChatMessage;
import com.example.mssaem_backend.domain.chatmessage.ChatMessage.MessageType;
import com.example.mssaem_backend.global.common.Time;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ChatMessageResourceDto {

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageRes {

        private Long chatRoomId;
        private Long worryBoardId;
        private MessageType type;
        private String sender;
        private String message;
        private String createdAt;

        public ChatMessageRes(ChatMessage chatMessage, String createdAt) {
            this.chatRoomId = chatMessage.getChatRoom().getId();
            this.worryBoardId = chatMessage.getChatRoom().getWorryBoardId();
            this.type = chatMessage.getType();
            this.sender = chatMessage.getSender();
            this.message = chatMessage.getMessage();
            this.createdAt = createdAt;
        }
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NowChatMessageRes {

        private Long chatRoomId;
        private String message;
        private String createdAt;
        private String sender;
        private int sendWho;

        public NowChatMessageRes(ChatMessage message, int sendWho) {
            this.chatRoomId = message.getChatRoom().getId();
            this.message = message.getMessage();
            this.createdAt = Time.calculateTime(message.getCreatedAt(), 4);
            this.sender = message.getSender();
            this.sendWho = sendWho;
        }
    }
}
