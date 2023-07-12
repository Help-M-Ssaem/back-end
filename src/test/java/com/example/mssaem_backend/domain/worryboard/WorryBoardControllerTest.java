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
    Member member1 = new Member(1L, "junsuck@naver.com", "heron", MbtiEnum.ENFJ, true,
        "tokenExample", Role.ROLE_MANAGER, "1234", "1100", "example1", 0);
    Member member2 = new Member(2L, "Jinro@naver.com", "Jinro", MbtiEnum.ENFJ, true,
        "tokenExample", Role.ROLE_MANAGER, "1234", "1100", "example2", 0);

    //worryBoard
    //해결 안된 고민
    WorryBoard worryBoard1 = WorryBoard.builder()
        .title("title1")
        .content("content1")
        .targetMbti(MbtiEnum.ENFJ)
        .state(false)
        .member(member1)
        .build();
    //해결이 된 고민
    WorryBoard worryBoard2 = WorryBoard.builder()
        .title("title2")
        .content("content2")
        .targetMbti(MbtiEnum.ENFP)
        .state(false)
        .member(member1)
        .build();
    worryBoard2.setSolveMember(member2);

    //worryBoardImage
    WorryBoardImage worryBoardImage1 = new WorryBoardImage(1L, worryBoard1, "imgUrl1");
    WorryBoardImage worryBoardImage2 = new WorryBoardImage(2L, worryBoard1, "imgUrl2");
    WorryBoardImage worryBoardImage3 = new WorryBoardImage(3L, worryBoard2, "imgUrl3");
    WorryBoardImage worryBoardImage4 = new WorryBoardImage(4L, worryBoard2, "imgUrl4");

    //save
    memberRepository.save(member1);
    memberRepository.save(member2);
    worryBoardRepository.save(worryBoard1);
    worryBoardRepository.save(worryBoard2);
    worryBoardImageRepository.save(worryBoardImage1);
    worryBoardImageRepository.save(worryBoardImage2);
    worryBoardImageRepository.save(worryBoardImage3);
    worryBoardImageRepository.save(worryBoardImage4);
  }
  @BeforeEach
  public void mockMvcSetup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
        .build();
  }

  @AfterEach
  public void AfterEach() {

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
        .andExpect(jsonPath("$[0].memberMbti").value("ENFJ"))
        .andExpect(jsonPath("$[0].imgUrl").value("imgUrl1"))
        .andExpect(jsonPath("$[1].title").value("title2"))
        .andExpect(jsonPath("$[1].targetMbti").value("ENFP"))
        .andExpect(jsonPath("$[1].imgUrl").value("imgUrl3"));
  }
}