package com.example.mssaem_backend.global.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Time {

  public static String calculateTime(LocalDateTime dateTime, int displayTimeWay) {
    LocalDateTime currentTime = LocalDateTime.now();
    long minutes = ChronoUnit.MINUTES.between(dateTime, currentTime);
    long hours = ChronoUnit.HOURS.between(dateTime, currentTime);

    if (displayTimeWay == 1) { //날짜만 표시, (홈화면 HOT 게시글, 홈화면 HOT 토론)
      return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    } else if (displayTimeWay == 2) { //날짜와 시간 표시 (게시판 상세 조회, 고민글 상세 조회)
      return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    } else { //실시간 반영 시간 표시 (게시판 전체 조회, 고민 전체 조회, 댓글 전체 조회)
      if (minutes < 60) {
        return minutes + "분 전";
      } else if (hours < 24) {
        return hours + "시간 전";
      } else {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
      }
    }
  }
}
