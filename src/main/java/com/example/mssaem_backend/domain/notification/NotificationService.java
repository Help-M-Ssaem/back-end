package com.example.mssaem_backend.domain.notification;

import static com.example.mssaem_backend.global.common.Time.calculateTime;

import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.notification.dto.NotificationResponseDto.NotificationInfo;
import com.example.mssaem_backend.global.common.dto.PageResponseDto;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.NotificationErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private static final int CONTENT_LENGTH_LIMIT = 25;

    // 알림 등록 (게시글 댓글, 토론 댓글, hot 게시글, hot 토론, 대댓글)
    @Transactional
    public void createNotification(Long resourceId, String previewContent,
        NotificationType notificationType, Member receiver) {
        notificationRepository.save(
            new Notification(resourceId,
                previewContent,
                notificationType,
                receiver
            )
        );
    }

    // 알림 조회
    public PageResponseDto<List<NotificationInfo>> findNotifications(Member member, int page,
        int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Notification> notifications = notificationRepository.findByMember(member, pageRequest);

        return new PageResponseDto<>(
            notifications.getNumber(),
            notifications.getTotalPages(),
            setNotificationInfo(
                notifications
                    .stream()
                    .collect(Collectors.toList()))
        );
    }

    // 알림 읽음 처리
    @Transactional
    public String checkNotification(Long id) {
        notificationRepository.findById(id)
            .orElseThrow(() -> new BaseException(NotificationErrorCode.EMPTY_NOTIFICATION))
            .updateState(true);
        return "알림 읽기 완료";
    }

    // 알림 전체 읽기
    @Transactional
    public String checkAllNotifications(Member member) {
        for (Notification notification : notificationRepository.findByMemberAndStateIsFalse(
            member)) {
            notification.updateState(true);
        }
        return "알림 전체 읽기 완료";
    }

    // 알림 삭제하기 (읽은 알림만 가능)
    @Transactional
    public String deleteNotification(Long id) {
        notificationRepository.delete(notificationRepository.findByIdAndStateIsTrue(id)
            .orElseThrow(() -> new BaseException(NotificationErrorCode.EMPTY_NOTIFICATION)));
        return "삭제 완료";
    }

    // 알림 전체 삭제하기
    @Transactional
    public String deleteAllNotifications(Member member) {
        notificationRepository.deleteAll(notificationRepository.findByMember(member));
        return "알림 전체 삭제 완료";
    }


    // 알림 조회시 Dto 매핑하는 메소드
    private List<NotificationInfo> setNotificationInfo(List<Notification> notifications) {
        List<NotificationInfo> notificationInfos = new ArrayList<>();

        for (Notification notification : notifications) {
            notificationInfos.add(
                new NotificationInfo(
                    notification.getId(),
                    notification.getResourceId(),
                    notification.getNotificationType().getName(),
                    notification.getContent().length() > CONTENT_LENGTH_LIMIT ?
                        notification.getContent().substring(0, CONTENT_LENGTH_LIMIT)
                        : notification.getContent(),
                    calculateTime(notification.getCreatedAt(), 4),
                    notification.isState(),
                    notification.getNotificationType()
                )
            );
        }
        return notificationInfos;
    }

}
