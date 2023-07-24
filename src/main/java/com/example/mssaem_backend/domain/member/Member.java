package com.example.mssaem_backend.domain.member;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@DynamicInsert
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

    private boolean status = true;

    @NotNull
    private String refreshToken;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull
    private String caseSensitivity; //대소문자 구분

    private String profileImageUrl;

    private String introduction;

    @ColumnDefault("0")
    private Integer report; //신고수

    public void changeRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getDetailMbti() {
        char[] charArray = getMbti().toString().toCharArray();
        String caseSensitivity = getCaseSensitivity();

        for (int i = 0; i < caseSensitivity.length(); i++) {
            if (caseSensitivity.charAt(i) == '0') {
                charArray[i] = Character.toLowerCase(charArray[i]);
            }
        }
        return String.valueOf(charArray);
    }

}
