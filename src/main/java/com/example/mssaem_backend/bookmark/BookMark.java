package com.example.mssaem_backend.bookmark;

import com.example.mssaem_backend.MbtiEnum;
import com.example.mssaem_backend.member.Member;
import com.example.mssaem_backend.utils.BaseTimeEntity;
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
public class BookMark extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_mark_id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MbtiEnum mbti;

    @ColumnDefault("false")
    private boolean state; //true : 즐겨찾기 중, false : 즐겨찾기 아님

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}
