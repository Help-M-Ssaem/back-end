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

    private String badgeName;

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

    public Member(String email, String nickName, MbtiEnum mbti, String caseSensitivity) {
        this.email = email;
        this.nickName = nickName;
        this.mbti = mbti;
        this.caseSensitivity = caseSensitivity;
        this.profileImageUrl = mbti.getProfileUrl();
        this.refreshToken = "";
        this.report = 0;
        this.role = Role.ROLE_MEMBER;
    }

    public void modifyMember(String nickName, String introduction, String profileImageUrl,
                             MbtiEnum mbti, String caseSensitivity, String badgeName) {
        this.nickName = nickName != null ? nickName : this.nickName;
        this.introduction = introduction != null ? introduction : this.introduction;
        this.profileImageUrl = profileImageUrl != null ? profileImageUrl : this.profileImageUrl;
        this.mbti = mbti != null ? mbti : this.mbti;
        this.caseSensitivity = caseSensitivity != null ? caseSensitivity : this.caseSensitivity;
        this.badgeName = badgeName != null ? badgeName : this.badgeName;

    }

    public Integer increaseReport() {
        return this.report++;
    }

    public void updateStatus() {
        this.status = false;
    }

}
