package com.example.mssaem_backend.domain.chatroom.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatRoomRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomInfo {

        private Long worryBoardId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatInfo implements Serializable {

        private Long chatRoomId;
        private String sender;
    }
}
