package com.example.mssaem_backend.domain.worryboard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorryBoardRepository extends JpaRepository<WorryBoard, Long> {

    Page<WorryBoard> findByState(boolean state,  Pageable pageable);

    Page<WorryBoard> findByMemberId(Long memberId, Pageable pageable);

    Page<WorryBoard> findBySolveMemberId(Long memberId, Pageable pageable);
}
