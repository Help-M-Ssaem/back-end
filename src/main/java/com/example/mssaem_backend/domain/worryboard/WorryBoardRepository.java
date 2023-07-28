package com.example.mssaem_backend.domain.worryboard;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.Member;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorryBoardRepository extends JpaRepository<WorryBoard, Long> {

    Page<WorryBoard> findByIsSolvedAndStateTrueOrderByCreatedAtDesc(boolean isSolved, Pageable pageable);

    Page<WorryBoard> findByMemberIdAndStateTrueOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    Page<WorryBoard> findBySolveMemberIdAndStateTrueOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    List<WorryBoard> findTop7ByIsSolvedFalseAndStateTrueOrderByCreatedAtDesc();

    WorryBoard findTopByStateFalseOrderByCreatedAtDesc();

    @Query("SELECT wb FROM WorryBoard wb WHERE wb.state = true AND wb.isSolved = :isSolved AND (:fromMbti IS NULL OR wb.member.mbti = :fromMbti) AND (:toMbti IS NULL OR wb.targetMbti = :toMbti) ORDER BY wb.createdAt DESC")
    Page<WorryBoard> findWorriesBySolvedAndBothMbtiAndStateTrue(
        @Param("isSolved") Boolean isSolved,
        @Param("fromMbti") MbtiEnum fromMbti,
        @Param("toMbti") MbtiEnum toMbti,
        Pageable pageable
    );

    @Query("SELECT wb.solveMember FROM WorryBoard wb WHERE wb.solvedAt >= :oneMonthAgo AND wb.state = true GROUP BY wb.solveMember.id HAVING COUNT(wb.solveMember.id) >= 1 ORDER BY COUNT(wb.solveMember.id) DESC, wb.solveMember.id")
    Page<Member> findSolveMemberWithMoreThanOneIdAndIsSolvedTrueAndStateTrue(
        @Param("oneMonthAgo") LocalDateTime oneMonthAgo, PageRequest pageRequest);

    // 고민글 검색하기
    @Query("SELECT wb FROM WorryBoard wb WHERE"
        + "(    (:type = 0 AND (LOWER(wb.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(wb.content) LIKE LOWER(CONCAT('%', :keyword, '%'))))"
        + " OR (:type = 1 AND LOWER(wb.title) LIKE LOWER(CONCAT('%', :keyword, '%')))"
        + " OR (:type = 2 AND LOWER(wb.content) LIKE LOWER(CONCAT('%', :keyword, '%')))"
        + " OR (:type = 3 AND LOWER(wb.member.nickName) LIKE LOWER(CONCAT('%', :keyword, '%'))) )"
        + " AND wb.isSolved = :isSolved AND (:fromMbti IS NULL OR wb.member.mbti = :fromMbti) AND (:toMbti IS NULL OR wb.targetMbti = :toMbti)ORDER BY wb.createdAt DESC ")
    Page<WorryBoard> searchWorriesBySolvedAndTypeAndMbti(
        @Param("type") int type,
        @Param("keyword") String keyword,
        @Param("isSolved") Boolean isSolved,
        @Param("fromMbti") MbtiEnum fromMbti,
        @Param("toMbti") MbtiEnum toMbti,
        Pageable pageable);

    Integer countAllByStateIsTrueAndMember(@Param("member") Member member);

    Integer countALlBySolveMember(@Param("solveMember") Member solveMember);

}