package com.example.mssaem_backend.domain.board;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import com.example.mssaem_backend.domain.member.Member;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // HOT 게시글 조회
    Page<Board> findBoardsByLikeCountGreaterThanEqualAndStateIsTrueOrderByCreatedAtDesc(Long likeCount,
        PageRequest pageRequest);


    //boardId가 입력되지 않으면 전체 게시글 조회, 입력되면 해당 게시글만 제외하고 전체 조회
    @Query("SELECT b FROM Board b WHERE b.state = true AND (:boardId IS NULL OR b.id <> :boardId) order by b.createdAt desc ")

    Page<Board> findAllByStateIsTrueAndIdOrderByCreatedAtDesc(@Param("boardId") Long boardId,
        Pageable pageable);

    Page<Board> findAllByStateIsTrueAndMbtiOrderByCreatedAtDesc(MbtiEnum mbtiEnum,
        Pageable pageable);

    Page<Board> findAllByMemberIdAndStateIsTrueOrderByCreatedAtDesc(Long memberId,
        Pageable pageable);

    // 검색하기
    @Query("SELECT b FROM Board b WHERE"
        + "(    (:type = 0 AND (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%'))))"
        + " OR (:type = 1 AND LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')))"
        + " OR (:type = 2 AND LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%')))"
        + " OR (:type = 3 AND LOWER(b.member.nickName) LIKE LOWER(CONCAT('%', :keyword, '%'))) )"
        + " AND b.state = true AND (:mbti IS NULL OR b.mbti = :mbti) ORDER BY b.createdAt DESC ")
    Page<Board> searchByTypeAndMbti(
        @Param("type") int type,
        @Param("keyword") String keyword,
        @Param("mbti") MbtiEnum mbti,
        Pageable pageable);

    Long countAllByStateIsTrueAndMember(@Param("member") Member member);

    Board findByMemberAndIdAndStateIsTrue(Member member, Long id);


    //내용, 제목, 닉네임 별 검색어 한 번에 조회
    @Query(value = "select b from Board b join fetch b.member"
        + " where (lower(b.title) like lower(concat('%', :keyword, '%'))) "
        + "or (lower(b.content) like lower(concat('%', :keyword, '%'))) "
        + "or (lower(b.member.nickName) like lower(concat('%', :keyword, '%'))) "
        + "and b.state = true order by b.createdAt desc",
        countQuery = "select count(b) from Board b")
    Page<Board> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Optional<Board> findByIdAndStateIsTrue(Long id);

    @Query(value = "SELECT SUM(b.likeCount) FROM Board b WHERE b.member = :member AND b.state = true")
    Long sumLikeCountByMember(@Param("member") Member member);

    Optional<Object> findStateTrueById(Long postId);
}
