package com.example.mssaem_backend.chatparticipate;

import com.example.mssaem_backend.chatroom.ChatRoom;
import com.example.mssaem_backend.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatParticipate {
    @Id
    @OneToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}
