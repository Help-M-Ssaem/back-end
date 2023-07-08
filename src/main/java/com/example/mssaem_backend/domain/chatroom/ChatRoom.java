package com.example.mssaem_backend.domain.chatroom;

import com.example.mssaem_backend.global.common.BaseTimeEntity;
import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatRoom extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ColumnDefault("true")
    private boolean state; //true : 열림, false : 닫힘

    @NotNull
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    private WorryBoard worryBoard;
}
