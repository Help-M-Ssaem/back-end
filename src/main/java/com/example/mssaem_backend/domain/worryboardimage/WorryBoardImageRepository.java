package com.example.mssaem_backend.domain.worryboardimage;

import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorryBoardImageRepository extends JpaRepository<WorryBoardImage, Long> {

    List<WorryBoardImage> findAllByWorryBoard(WorryBoard worryBoard);

    WorryBoardImage findTopByWorryBoardOrderById(WorryBoard worryBoard);

    void deleteAllByWorryBoard(WorryBoard worryBoard);

    void deleteWorryBoardImageByImgUrl(String imgUrl);
}