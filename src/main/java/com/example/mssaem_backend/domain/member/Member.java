package com.example.mssaem_backend.domain.member;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Builder
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
    private String refreshToken;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull
    private String password;

    @NotNull
    private String caseSensitivity; //대소문자 구분

    private String profileImageUrl;

    @ColumnDefault("0")
    private Integer report; //신고수

    public void changeRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
