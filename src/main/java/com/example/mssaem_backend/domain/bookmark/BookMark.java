package com.example.mssaem_backend.domain.bookmark;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
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
public class BookMark extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MbtiEnum mbti;

    private boolean state = true; //true : 즐겨찾기 중, false : 즐겨찾기 아님

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public BookMark(MbtiEnum mbti , Member member) {
        this.mbti = mbti;
        this.member = member;
    }

    public void updateBookMark() {
        //현재 상태가 true 라면 false로 변경
        this.state = !this.state;
    }

    public Boolean nowBookmarkState(){
        return this.state;
    }
}
