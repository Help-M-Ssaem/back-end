package com.example.mssaem_backend.domain.worryboard;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.MemberRepository;
import com.example.mssaem_backend.domain.member.Role;
import com.example.mssaem_backend.domain.worryboardimage.WorryBoardImage;
import com.example.mssaem_backend.domain.worryboardimage.WorryBoardImageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
class WorryBoardControllerTest {

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
  WorryBoardImageRepository worryBoardImageRepository;

  @BeforeAll
  void beforeAll() {
    //member
    Member member1 = new Member(1L, "junsuck@naver.com", "heron", MbtiEnum.INFP, true,
        "tokenExample", Role.ROLE_MANAGER, "1234", "1100", "example1", 0);
    Member member2 = new Member(2L, "Jinro@naver.com", "Jinro", MbtiEnum.ENTJ, true,
        "tokenExample", Role.ROLE_MANAGER, "1234", "1100", "example2", 0);

    //worryBoard
    //해결 안된 고민
    WorryBoard worryBoard1 = WorryBoard.builder()
        .title("title1")
        .content("content1")
        .targetMbti(MbtiEnum.ESFJ)
        .state(false)
        .member(member1)
        .build();
    WorryBoard worryBoard2 = WorryBoard.builder()
        .title("title2")
        .content("content2")
        .targetMbti(MbtiEnum.ENTP)
        .state(false)
        .member(member1)
        .build();
    //해결이 된 고민
    WorryBoard worryBoardSolved1 = WorryBoard.builder()
        .title("titleSolved1")
        .content("contentSolved1")
        .targetMbti(MbtiEnum.ENTJ)
        .state(true)
        .member(member1)
        .build();
    WorryBoard worryBoardSolved2 = WorryBoard.builder()
        .title("titleSolved2")
        .content("contentSolved2")
        .targetMbti(MbtiEnum.ENTJ)
        .state(true)
        .member(member1)
        .build();
    worryBoardSolved1.setSolveMember(member2);
    worryBoardSolved2.setSolveMember(member2);

    //worryBoardImage
    WorryBoardImage worryBoardImage1 = new WorryBoardImage(1L, worryBoard1, "imgUrl1");
    WorryBoardImage worryBoardImage2 = new WorryBoardImage(2L, worryBoard1, "imgUrl2");
    WorryBoardImage worryBoardImage3 = new WorryBoardImage(3L, worryBoard1, "imgUrl3");
    WorryBoardImage worryBoardImage4 = new WorryBoardImage(4L, worryBoard1, "imgUrl4");
    WorryBoardImage worryBoardImage5 = new WorryBoardImage(5L, worryBoardSolved1, "imgUrl5");
    WorryBoardImage worryBoardImage6 = new WorryBoardImage(6L, worryBoardSolved1, "imgUrl6");

    //save
    memberRepository.save(member1);
    memberRepository.save(member2);
    worryBoardRepository.save(worryBoard1);
    worryBoardRepository.save(worryBoard2);
    worryBoardRepository.save(worryBoardSolved1);
    worryBoardRepository.save(worryBoardSolved2);
    worryBoardImageRepository.save(worryBoardImage1);
    worryBoardImageRepository.save(worryBoardImage2);
    worryBoardImageRepository.save(worryBoardImage3);
    worryBoardImageRepository.save(worryBoardImage4);
    worryBoardImageRepository.save(worryBoardImage5);
    worryBoardImageRepository.save(worryBoardImage6);

  }
  @BeforeEach
  public void mockMvcSetup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
        .build();
  }

  @AfterEach
  public void AfterEach() {
    /*memberRepository.deleteAll();
    worryBoardRepository.deleteAll();
    worryBoardImageRepository.deleteAll();*/
  }

  @DisplayName("해결 기다리는 고민 조회")
  @Test
  public void findWorriesWaiting() throws Exception {
    //given
    final String url = "/worry-board/waiting";

    //when
    final ResultActions resultActions = mockMvc.perform(get(url)
        .accept(MediaType.APPLICATION_JSON));

    //then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("title1"))
        .andExpect(jsonPath("$[0].memberMbti").value("INFP"))
        .andExpect(jsonPath("$[0].imgUrl").value("imgUrl1"))
        .andExpect(jsonPath("$[1].title").value("title2"))
        .andExpect(jsonPath("$[1].targetMbti").value("ENTP"))
        .andExpect(jsonPath("$[1].imgUrl").value("default"));
  }

  @DisplayName("해결 완료된 고민 조회")
  @Test
  public void findWorriesSolved() throws Exception {
    //given
    final String url = "/worry-board/waiting";

    //when
    final ResultActions resultActions = mockMvc.perform(get(url)
        .accept(MediaType.APPLICATION_JSON));

    //then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("titleSolved1"))
        .andExpect(jsonPath("$[0].imgUrl").value("imgUrl5"))
        .andExpect(jsonPath("$[1].title").value("titleSolved2"));
  }

  @DisplayName("특정 멤버 별 올린 고민 조회")
  @Test
  public void findWorriesByMemberId() throws Exception {
    //given
    final String url = "/worry-board/post-list/1";

    //when
    final ResultActions resultActions = mockMvc.perform(get(url)
        .accept(MediaType.APPLICATION_JSON));

    //then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("title1"))
        .andExpect(jsonPath("$[1].title").value("title2"));
  }
}