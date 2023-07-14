package com.example.mssaem_backend.domain.worryboard;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorryBoardRepository extends JpaRepository<WorryBoard, Long> {

    Page<WorryBoard> findByState(boolean state, Pageable pageable);

    Page<WorryBoard> findByMemberId(Long memberId, Pageable pageable);

    Page<WorryBoard> findBySolveMemberId(Long memberId, Pageable pageable);

    // Mbti -> Mbti
    @Query("SELECT wb FROM WorryBoard wb WHERE wb.state = :state AND (wb.member.mbti = :fromMbti) AND (wb.targetMbti = :toMbti)")
    Page<WorryBoard> findWorriesByStateAndBothMbti(
        @Param("state") Boolean state,
        @Param("fromMbti") MbtiEnum fromMbti,
        @Param("toMbti") MbtiEnum toMbti,
        Pageable pageable
    );

    // Mbti -> ALL
    @Query("SELECT wb FROM WorryBoard wb WHERE wb.state = :state AND wb.member.mbti = :fromMbti")
    Page<WorryBoard> findWorriesByStateAndFromMbti(@Param("state") Boolean state,
        @Param("fromMbti") MbtiEnum fromMbti, Pageable pageable);

    // ALL -> Mbti
    @Query("SELECT wb FROM WorryBoard wb WHERE wb.state = :state AND wb.targetMbti = :toMbti")
    Page<WorryBoard> findWorriesByStateAndToMbti(@Param("state") Boolean state,
        @Param("toMbti") MbtiEnum toMbti, Pageable pageable);


}
