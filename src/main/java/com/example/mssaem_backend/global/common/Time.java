package com.example.mssaem_backend.global.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Time {

    public static String timesAgo(LocalDateTime dayBefore) {
        long gap = ChronoUnit.MINUTES.between(dayBefore, LocalDateTime.now());
        String word;
        if (gap == 0) {
            word = "방금 전";
        } else if (gap < 60) {
            word = gap + "분 전";
        } else if (gap < 60 * 24) {
            word = (gap / 60) + "시간 전";
        } else {
            word = dayBefore.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
        return word;
    }
}
