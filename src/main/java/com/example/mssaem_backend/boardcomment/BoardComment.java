package com.example.mssaem_backend.boardcomment;

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
public class BoardComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_comment_id")
    private Long id;

    @NotNull
    private String content;

    @ColumnDefault("0")
    private Long recommendation;

    @ColumnDefault("0")
    private Integer depth; //댓글 : 0, 대 댓글 : 1

    @ColumnDefault("0")
    private Integer parentId; //댓글 : 0, 대 댓글 : 자신의 부모 댓글 id

    @ColumnDefault("0")
    private Integer order; //대댓글의 순서

    @ColumnDefault("true")
    private boolean state; //true : 존재, false : 삭제

}
