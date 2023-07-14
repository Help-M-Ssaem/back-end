package com.example.mssaem_backend.domain.evaluation;

import com.example.mssaem_backend.domain.evaluation.dto.EvaluationRequestDto.EvaluationInfo;
import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.MemberRepository;
import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import com.example.mssaem_backend.domain.worryboard.WorryBoardRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
class EvaluationControllerTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper; //직렬화, 역직렬화 위한 클래스

  @Autowired
  private WebApplicationContext context;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  WorryBoardRepository worryBoardRepository;

  @Autowired
  EvaluationRepository evaluationRepository;

  //String email, String nickName, MbtiEnum mbti, String password, String caseSensitivity
  //String title, String content, MbtiEnum mbti
  @BeforeAll
  void beforeAll(){
    memberRepository.save(new Member("lympsw12@naver.com", "heron", MbtiEnum.ISTJ, "aaa", "0100"));
    worryBoardRepository.save(new WorryBoard("aaaa", "aaaa", MbtiEnum.ISFJ));
  }
  @BeforeEach
  public void mockMvcSetUp() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
        .build();
  }

  @AfterEach
  public void AfterEach(){
    evaluationRepository.deleteAll();
  }

  @DisplayName("insertEvaluation : 평가 추가 하기")
  @Test
  public void insertEvaluation() throws Exception {
    //given
    final String url = "/evaluations";
    final Long worryBoardId = 1L;
    final List<EvaluationEnum> enumList = new ArrayList<>();
    enumList.add(EvaluationEnum.LIKE);
    enumList.add(EvaluationEnum.HOT);
    final EvaluationInfo userRequest = new EvaluationInfo(worryBoardId, enumList);

    final String requestBody = objectMapper.writeValueAsString(userRequest);

    //when
    ResultActions result = mockMvc.perform(post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody));

    //then
    result.andExpect(status().isOk());

    List<Evaluation> evaluations = evaluationRepository.findAll();
    assertThat(evaluations.get(0).getEvaluationCode()).isEqualTo("10001");

  }

  @DisplayName("selectEvaluation : 평가 조회 하기")
  @Test
  public void selectEvaluation() throws Exception {
    // given
    final String url = "/evaluations/{worryBoardId}";
    WorryBoard worryBoard = worryBoardRepository.findById(1L).orElseThrow();
    Member member = memberRepository.findById(1L).orElseThrow();
    Evaluation evaluation = new Evaluation(worryBoard, member, "10001");
    evaluationRepository.save(evaluation);

    // when
    final ResultActions resultActions = mockMvc.perform(get(url, 1));

    //then
    resultActions
        .andExpect(status().isOk());
  }

  @DisplayName("countEvaluation : 평가 count 조회 하기")
  @Test
  public void countEvaluation() throws Exception {
    // given
    final String url = "/evaluations/count/{memberId}";
    WorryBoard worryBoard = worryBoardRepository.findById(1L).orElseThrow();
    Member member = memberRepository.findById(1L).orElseThrow();
    Evaluation evaluation = new Evaluation(worryBoard, member, "10001");
    evaluationRepository.save(evaluation);

    // when
    final ResultActions resultActions = mockMvc.perform(get(url, 1));

    //then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.likeCount").value(1));

  }



}