package com.example.mssaem_backend.domain.evaluation;

import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

  Evaluation findByWorryBoardAndMember(WorryBoard worryBoard, Member member);

  List<Evaluation> findAllByMember(Member member);

  Integer countAllByMember(Member member);
}
