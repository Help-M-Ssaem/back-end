package com.example.mssaem_backend.domain.chatroom;

import com.example.mssaem_backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
public class ChatRoom extends BaseTimeEntity implements Serializable {
    @Id
    @Column(name = "chatroom_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private static final long serialVersionUID = 6494678977089006639L;

    @ColumnDefault("true")
    private boolean state; //true : 열림, false : 닫힘

    @NotNull
    private String title;

    @NotNull
    private Long worryBoardId;

    public static ChatRoom create(String title, Long worryBoardId) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.title = title;
        chatRoom.worryBoardId = worryBoardId;
        return chatRoom;
    }
}
