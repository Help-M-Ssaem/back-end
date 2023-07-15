package com.example.mssaem_backend.domain.evaluation;


import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.common.BaseTimeEntity;
import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Evaluation extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private WorryBoard worryBoard;

  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  @NotNull
  private String evaluationCode;

  public Evaluation(WorryBoard worryBoard, Member member, String evaluationCode) {
    this.worryBoard = worryBoard;
    this.member = member;
    this.evaluationCode = evaluationCode;
  }
}
