package com.example.mssaem_backend.domain.notification;

import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.common.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@DynamicInsert
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long resourceId;

    @NotNull
    private String content;

    private boolean state; // true : 읽음, false : 안 읽음

    @NotNull
    @Enumerated(EnumType.STRING)
    private TypeEnum type;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member; // 알림을 받을 멤버

    public Notification(Long resourceId, String content, TypeEnum type, Member member) {
        this.resourceId = resourceId;
        this.content = content;
        this.type = type;
        this.member = member;
    }

    public void updateState(boolean state) {
        this.state = state;
    }
}
