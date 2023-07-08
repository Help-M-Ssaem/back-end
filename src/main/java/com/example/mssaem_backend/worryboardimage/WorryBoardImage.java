package com.example.mssaem_backend.worryboardimage;

import com.example.mssaem_backend.worryboard.WorryBoard;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class WorryBoardImage{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "worry_board_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private WorryBoard worryBoard;

    private String imgUrl;
}
