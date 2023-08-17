package com.example.mssaem_backend.domain.evaluation;

import com.example.mssaem_backend.domain.badge.Badge;
import com.example.mssaem_backend.domain.badge.BadgeEnum;
import com.example.mssaem_backend.domain.badge.BadgeService;
import com.example.mssaem_backend.domain.chatparticipate.ChatParticipateService;
import com.example.mssaem_backend.domain.chatroom.ChatRoom;
import com.example.mssaem_backend.domain.chatroom.ChatRoomService;
import com.example.mssaem_backend.domain.evaluation.dto.EvaluationRequestDto.EvaluationInfo;
import com.example.mssaem_backend.domain.evaluation.dto.EvaluationResultDto.EvaluationCount;
import com.example.mssaem_backend.domain.evaluation.dto.EvaluationResultDto.EvaluationResult;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.notification.NotificationService;
import com.example.mssaem_backend.domain.notification.NotificationType;
import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import com.example.mssaem_backend.domain.worryboard.WorryBoardRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class EvaluationService {

    private final WorryBoardRepository worryBoardRepository;
    private final EvaluationRepository evaluationRepository;
    private final BadgeService badgeService;
    private final NotificationService notificationService;
    private final ChatParticipateService chatParticipateService;
    private final ChatRoomService chatRoomService;

    /**
     * 평가 추가 하기
     */
    public String insertEvaluation(Member member, EvaluationInfo evaluationInfo) {
        //현재 고민글 조회
        WorryBoard worryBoard = worryBoardRepository.findById(evaluationInfo.getWorryBoardId())
            .orElseThrow();
        //상대 조회
        ChatRoom chatRoom = chatRoomService.selectChatRoomByWorryBoardId(
            evaluationInfo.getWorryBoardId());
        Member partner = chatParticipateService.getPartnerByChatRoomAndMember(
            member, chatRoom);

        //평가 리스트 체크
        String[] checks = new String[5];
        Arrays.fill(checks, "0");
        evaluationInfo.getEvaluations().forEach(e -> {
            if (e.equals(EvaluationEnum.LIKE)) {
                checks[0] = "1";
            } else if (e.equals(EvaluationEnum.USEFUL)) {
                checks[1] = "1";
            } else if (e.equals(EvaluationEnum.FUN)) {
                checks[2] = "1";
            } else if (e.equals(EvaluationEnum.SINCERE)) {
                checks[3] = "1";
            } else if (e.equals(EvaluationEnum.HOT)) {
                checks[4] = "1";
            }
        });
        String result = Arrays.stream(checks).collect(Collectors.joining());
        evaluationRepository.save(new Evaluation(worryBoard, partner, result));

        // Badge추가 및 알림 추가
        insertBadgeAndNotification(partner);
        return "펑가 완료";
    }

    /**
     * Badge 추가 및 알림 추가
     */
    public void insertBadgeAndNotification(Member partner) {
        EvaluationCount evaluationCount = countEvaluation(partner);
        boolean check = true;
        if (badgeService.existBadgeStateTrue(partner)) {
            check = false;
        }
        if (evaluationCount.getFunCount() == BadgeEnum.FUNFUN.getStandard()) {
            Badge badge = new Badge(BadgeEnum.FUNFUN, partner, check);
            badgeService.insertBadge(badge);
            notificationService.createNotification(badge.getId(), badge.getBadgeEnum().getName(),
                NotificationType.BADGE, partner);
        } else if (evaluationCount.getHotCount() == BadgeEnum.MBTIRANO.getStandard()) {
            Badge badge = new Badge(BadgeEnum.MBTIRANO, partner, check);
            badgeService.insertBadge(badge);
            notificationService.createNotification(badge.getId(), badge.getBadgeEnum().getName(),
                NotificationType.BADGE, partner);
        } else if (evaluationCount.getUsefulCount() == BadgeEnum.MBTADULT.getStandard()) {
            Badge badge = new Badge(BadgeEnum.MBTADULT, partner, check);
            badgeService.insertBadge(badge);
            notificationService.createNotification(badge.getId(), badge.getBadgeEnum().getName(),
                NotificationType.BADGE, partner);
        } else if (evaluationCount.getSincereCount() == BadgeEnum.MBTMI.getStandard()) {
            Badge badge = new Badge(BadgeEnum.MBTMI, partner, check);
            badgeService.insertBadge(badge);
            notificationService.createNotification(badge.getId(), badge.getBadgeEnum().getName(),
                NotificationType.BADGE, partner);
        }
    }

    /**
     * 평가 받은 사람의 평가 내용 조회
     */
    public EvaluationResult selectEvaluation(Member member, Long worryBoardId) {
        //고민글 조회
        WorryBoard worryBoard = worryBoardRepository.findById(worryBoardId)
            .orElseThrow();
        Evaluation evaluation = evaluationRepository.findByWorryBoardAndMember(worryBoard, member);

        //자신이 받은 평가 출력
        List<EvaluationEnum> result = new ArrayList<>();
        EvaluationEnum[] enums = EvaluationEnum.values();
        for (int i = 0; i < evaluation.getEvaluationCode().length(); i++) {
            if (evaluation.getEvaluationCode().charAt(i) == '1') {
                result.add(enums[i]);
            }
        }
        return new EvaluationResult(worryBoardId, result);
    }

    /**
     * 자신의 평가 count
     */
    public EvaluationCount countEvaluation(Member member) {
        List<Evaluation> evaluations = evaluationRepository.findAllByMember(member);
        int[] result = new int[EvaluationEnum.values().length];
        for (Evaluation e : evaluations) {
            char[] temp = e.getEvaluationCode().toCharArray();
            result[0] += temp[0] - '0';
            result[1] += temp[1] - '0';
            result[2] += temp[2] - '0';
            result[3] += temp[3] - '0';
            result[4] += temp[4] - '0';
        }
        return new EvaluationCount(result);
    }
}
