package com.example.mssaem_backend.domain.evaluation;

import com.example.mssaem_backend.domain.evaluation.dto.EvaluationRequestDto.EvaluationInfo;
import com.example.mssaem_backend.domain.evaluation.dto.EvaluationResultDto.EvaluationCount;
import com.example.mssaem_backend.domain.evaluation.dto.EvaluationResultDto.EvaluationResult;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.MemberRepository;
import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import com.example.mssaem_backend.domain.worryboard.WorryBoardRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    //평가 리스트 체크
    String[] checks = new String[5];
    Arrays.fill(checks, "0");
    evaluationInfo.getEvaluations().forEach(e -> {
      if (e.equals(EvaluationEnum.LIKE)) {
        checks[0] = "1";
      } else if (e.equals(EvaluationEnum.USEFUL)) {
        checks[1] = "1";
      } else if (e.equals(EvaluationEnum.FUN)) {
        checks[2] = "1";
      } else if (e.equals(EvaluationEnum.SINCERE)) {
        checks[3] = "1";
      } else if (e.equals(EvaluationEnum.HOT)) {
        checks[4] = "1";
      }
    });
    String result = Arrays.stream(checks).collect(Collectors.joining());
    evaluationRepository.save(new Evaluation(worryBoard, member, result));
    return "펑가 완료";
  }

  /**
   * 평가 받은 사람의 평가 내용 조회
   */
  public EvaluationResult selectEvaluation(Long worryBoardId) {
    //유저 임시 조회
    Member member = memberRepository.findById(1L).orElseThrow();
    //고민글 조회
    WorryBoard worryBoard = worryBoardRepository.findById(worryBoardId)
        .orElseThrow();
    Evaluation evaluation = evaluationRepository.findByWorryBoardAndMember(worryBoard, member);

    //자신이 받은 평가 출력
    List<EvaluationEnum> result = new ArrayList<>();
    EvaluationEnum[] enums = EvaluationEnum.values();
    for (int i = 0; i < evaluation.getEvaluationCode().length(); i++) {
      if (evaluation.getEvaluationCode().charAt(i) == '1') {
        result.add(enums[i]);
      }
    }
    return new EvaluationResult(worryBoardId, result);
  }

  /**
   * 자신의 평가 count
   */
  public EvaluationCount countEvaluation(Long memberId) {
    //유저 임시 조회
    Member member = memberRepository.findById(1L).orElseThrow();

    List<Evaluation> evaluations = evaluationRepository.findAllByMember(member);

    int[] result = new int[5];
    for (Evaluation e : evaluations) {
      char[] temp = e.getEvaluationCode().toCharArray();
      result[0] += temp[0] - '0';
      result[1] += temp[1] - '0';
      result[2] += temp[2] - '0';
      result[3] += temp[3] - '0';
      result[4] += temp[4] - '0';
    }
    return new EvaluationCount(member, result);
  }
}
