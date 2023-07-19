package com.example.mssaem_backend.domain.worryboard;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorryBoardRepository extends JpaRepository<WorryBoard, Long> {

    Page<WorryBoard> findByState(boolean state, Pageable pageable);

    Page<WorryBoard> findByMemberId(Long memberId, Pageable pageable);

    Page<WorryBoard> findBySolveMemberId(Long memberId, Pageable pageable);

    List<WorryBoard> findTop7ByStateFalseOrderByCreatedAtDesc();

    @Query("SELECT wb FROM WorryBoard wb WHERE wb.state = :state AND (:fromMbti IS NULL OR wb.member.mbti = :fromMbti) AND (:toMbti IS NULL OR wb.targetMbti = :toMbti)")
    Page<WorryBoard> findWorriesByStateAndBothMbti(
        @Param("state") Boolean state,
        @Param("fromMbti") MbtiEnum fromMbti,
        @Param("toMbti") MbtiEnum toMbti,
        Pageable pageable
    );
}