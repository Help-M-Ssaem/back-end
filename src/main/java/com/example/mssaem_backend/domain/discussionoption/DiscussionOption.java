package com.example.mssaem_backend.domain.discussionoption;

import com.example.mssaem_backend.domain.discussion.Discussion;
import com.example.mssaem_backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@DynamicInsert
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DiscussionOption extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imgUrl;

    private String content;

    @ColumnDefault("0")
    private Long selectCount; // 선택한 사람 수

    @ManyToOne(fetch = FetchType.LAZY)
    private Discussion discussion;

    @Builder
    public DiscussionOption(String content, String imgUrl, Discussion discussion) {
        this.content = content;
        this.imgUrl = imgUrl;
        this.discussion = discussion;
    }

    public void increaseCount() {
        this.selectCount += 1;
    }

    public void decreaseCount() {
        this.selectCount -= 1;
    }
}