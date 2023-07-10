package com.example.mssaem_backend.domain.evaluation;

import com.example.mssaem_backend.domain.evaluation.dto.EvaluationRequestDto.EvaluationInfo;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.MemberRepository;
import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import com.example.mssaem_backend.domain.worryboard.WorryBoardRepository;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class EvaluationService {

  private final MemberRepository memberRepository;
  private final WorryBoardRepository worryBoardRepository;
  private final EvaluationRepository evaluationRepository;

  /**
   * 평가 추가 하기
   */
  public String insertEvaluation(EvaluationInfo evaluationInfo) {
    //유저 임시 조회
    Member member = memberRepository.findById(1L).orElseThrow();
    //현재 고민글 조회
    WorryBoard worryBoard = worryBoardRepository.findById(evaluationInfo.getWorryBoardId())
        .orElseThrow();
    //EmumList를 evaluationCode로 변경
    String[] checks = new String[5];
    Arrays.fill(checks, "0");
    evaluationInfo.getEvaluations().forEach(e -> {
      if(e.equals(EvaluationEnum.LIKE)) checks[0] = "1";
      else if(e.equals(EvaluationEnum.USEFUL)) checks[1] = "1";
      else if(e.equals(EvaluationEnum.FUN)) checks[2] = "1";
      else if(e.equals(EvaluationEnum.SINCERE)) checks[3] = "1";
      else if(e.equals(EvaluationEnum.HOT)) checks[4] = "1";
    });
    String result = Arrays.stream(checks).collect(Collectors.joining());
    evaluationRepository.save(new Evaluation(worryBoard, member, result));
    return "펑가 완료";
  }


}
