package com.example.mssaem_backend.chatparticipate;

import com.example.mssaem_backend.chatroom.ChatRoom;
import com.example.mssaem_backend.member.Member;
import com.example.mssaem_backend.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatParticipate extends BaseTimeEntity {
    @Id
    @OneToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}
