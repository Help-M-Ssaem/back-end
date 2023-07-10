package com.example.mssaem_backend.domain.member;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotNull
    private String email;

    @NotNull
    private String nickName;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MbtiEnum mbti;

    @ColumnDefault("false")
    private boolean status;

    @NotNull
    private String password;

    @NotNull
    private String caseSensitivity; //대소문자 구분

    private String profileImageUrl;

    private Integer report; //신고수

    //Test용 생성자(@Notnull 인 것만)
    public Member(String email, String nickName, MbtiEnum mbti, String password, String caseSensitivity) {
        this.email = email;
        this.nickName = nickName;
        this.mbti = mbti;
        this.password = password;
        this.caseSensitivity = caseSensitivity;
    }
}
