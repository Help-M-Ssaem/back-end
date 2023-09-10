package com.example.mssaem_backend.domain.badge;

import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
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

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Badge extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private BadgeEnum badgeEnum;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private boolean state; // true : 대표

    public void changeStateTrue() {
        this.state = true;
    }

    public void changeStateFalse() {
        this.state = false;
    }

    public Badge(BadgeEnum badgeEnum, Member member, boolean state){
        this.badgeEnum = badgeEnum;
        this.member = member;
        this.state = state;
    }
}
