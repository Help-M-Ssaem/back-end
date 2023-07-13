package com.example.mssaem_backend.domain.worryboard;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorryBoardRepository extends JpaRepository<WorryBoard, Long> {

  List<WorryBoard> findByState(boolean state);
  List<WorryBoard> findByMemberId(Long memberId);
}
