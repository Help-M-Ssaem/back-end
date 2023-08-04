package com.example.mssaem_backend.domain.notification;

import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.notification.dto.NotificationResponseDto.NotificationInfo;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 알림 목록 조회
     */
    @GetMapping("/member/notifications")
    public ResponseEntity<PageResponseDto<List<NotificationInfo>>> findNotifications(
        @CurrentMember Member member,
        @RequestParam int page,
        @RequestParam int size) {
        return ResponseEntity.ok(notificationService.findNotifications(member, page, size));
    }

    /**
     * 알림 읽기
     */
    @PatchMapping("/member/notifications")
    public ResponseEntity<String> checkNotification(@RequestParam Long id) {
        return ResponseEntity.ok(notificationService.checkNotification(id));
    }

    /**
     * 알림 전체 읽기
     */
    @PatchMapping("/member/notifications/all")
    public ResponseEntity<String> checkAllNotifications(@CurrentMember Member member) {
        return ResponseEntity.ok(notificationService.checkAllNotifications(member));
    }

    /**
     * 알림 삭제하기 (읽은 알림만 가능)
     */
    @DeleteMapping("/member/notifications")
    public ResponseEntity<String> deleteNotification(@RequestParam Long id) {
        return ResponseEntity.ok(notificationService.deleteNotification(id));
    }

    /**
     * 알림 전체 삭제하기
     */
    @DeleteMapping("/member/notifications/all")
    public ResponseEntity<String> deleteAllNotification(@CurrentMember Member member) {
        return ResponseEntity.ok(notificationService.deleteAllNotifications(member));
    }

}
