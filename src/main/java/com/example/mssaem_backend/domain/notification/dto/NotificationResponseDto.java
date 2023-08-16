package com.example.mssaem_backend.domain.notification.dto;

import com.example.mssaem_backend.domain.notification.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NotificationResponseDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationInfo {

        private Long id; // 알림 id
        private Long resourceId; // 알림 대상 id
        private String notificationTypeContent; // 알림 타입별 멘트
        private String content; // 알림 내용
        private String createdAt; // 오늘 알림 : 오전/오후 시간, 오늘 이전 알림 : 0월 0일
        private boolean state; // true : 읽음, false : 안 읽음
        private NotificationType notificationType; // 알림의 타입
    }

}
