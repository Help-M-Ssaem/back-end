package com.example.mssaem_backend.domain.board;

import com.example.mssaem_backend.domain.mbti.MbtiEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Page<Board> findAllByStateIsTrue(Pageable pageable);

    Page<Board> findAllByStateIsTrueAndMbti(MbtiEnum mbtiEnum, Pageable pageable);

    Page<Board> findAllByMemberId(Long memberId, Pageable pageable);
}
