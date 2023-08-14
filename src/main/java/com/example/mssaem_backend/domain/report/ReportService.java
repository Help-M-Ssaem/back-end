package com.example.mssaem_backend.domain.report;

import com.example.mssaem_backend.domain.board.Board;
import com.example.mssaem_backend.domain.board.BoardRepository;
import com.example.mssaem_backend.domain.boardcomment.BoardComment;
import com.example.mssaem_backend.domain.boardcomment.BoardCommentRepository;
import com.example.mssaem_backend.domain.discussion.Discussion;
import com.example.mssaem_backend.domain.discussion.DiscussionRepository;
import com.example.mssaem_backend.domain.discussioncomment.DiscussionComment;
import com.example.mssaem_backend.domain.discussioncomment.DiscussionCommentRepository;
import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.member.MemberRepository;
import com.example.mssaem_backend.domain.report.dto.ReportRequestDto.ReportReq;
import com.example.mssaem_backend.domain.worryboard.WorryBoard;
import com.example.mssaem_backend.domain.worryboard.WorryBoardRepository;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.BoardCommentErrorCode;
import com.example.mssaem_backend.global.config.exception.errorCode.BoardErrorCode;
import com.example.mssaem_backend.global.config.exception.errorCode.DiscussionCommentErrorCode;
import com.example.mssaem_backend.global.config.exception.errorCode.DiscussionErrorCode;
import com.example.mssaem_backend.global.config.exception.errorCode.MemberErrorCode;
import com.example.mssaem_backend.global.config.exception.errorCode.ReportError;
import com.example.mssaem_backend.global.config.exception.errorCode.WorryBoardErrorCode;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final JavaMailSender javaMailSender;
    private final ReportRepository reportRepository;
    private final BoardRepository boardRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final DiscussionRepository discussionRepository;
    private final DiscussionCommentRepository discussionCommentRepository;
    private final WorryBoardRepository worryBoardRepository;
    private final MemberRepository memberRepository;

    private static final Integer REPORT_STANDARD = 10;
    private static final int REASON_SIZE = 6;

    @Transactional
    public String report(Member member, ReportReq reportReq) throws MessagingException {
        Report prevReport = reportRepository.findTopByResourceIdAndReportTargetAndMemberOrderByIdDesc(
            reportReq.getResourceId(),
            reportReq.getReportTarget(),
            member);

        //  신고 테이블에 이미 같은 신고가 있고, 일주일 이내인 경우 신고 못함
        if (prevReport != null && LocalDateTime.now().minusWeeks(1)
            .isBefore(prevReport.getCreatedAt())) {
            throw new BaseException(ReportError.DUPLICATE_REPORT);
        }

        reportRepository.save(
            Report.builder()
                .resourceId(reportReq.getResourceId())
                .reportTarget(reportReq.getReportTarget())
                .reportReason(reportReq.getReportReason())
                .content(reportReq.getContent())
                .member(member)
                .build()
        );

        // 각 타입별로 신고 대상의 report 수를 +1해주고, 누적 신고 수가 reportStandard인 경우 state를 false로 처리후 안내 메일 전송
        switch (reportReq.getReportTarget()) {
            case BOARD -> {
                Board board = boardRepository.findByIdAndStateIsTrue(reportReq.getResourceId())
                    .orElseThrow(() -> new BaseException(BoardErrorCode.EMPTY_BOARD));

                board.increaseReport();
                if (board.getReport().equals(REPORT_STANDARD)) {
                    board.updateState();
                    sendReportEmail(board.getMember().getEmail(), reportReq);
                }
            }
            case DISCUSSION -> {
                Discussion discussion = discussionRepository.findByIdAndStateIsTrue(
                        reportReq.getResourceId())
                    .orElseThrow(() -> new BaseException(DiscussionErrorCode.EMPTY_DISCUSSION));

                discussion.increaseReport();
                if (discussion.getReport().equals(REPORT_STANDARD)) {
                    discussion.updateState();
                    sendReportEmail(discussion.getMember().getEmail(), reportReq);
                }
            }
            case WORRY -> {
                WorryBoard worryBoard = worryBoardRepository.findByIdAndStateIsTrue(
                        reportReq.getResourceId())
                    .orElseThrow(() -> new BaseException(WorryBoardErrorCode.EMPTY_WORRY_BOARD));

                worryBoard.increaseReport();
                if (worryBoard.getReport().equals(REPORT_STANDARD)) {
                    worryBoard.updateState();
                    sendReportEmail(worryBoard.getMember().getEmail(), reportReq);
                }
            }
            case BOARD_COMMENT -> {
                BoardComment boardComment = boardCommentRepository.findByIdAndStateIsTrue(
                    reportReq.getResourceId()).orElseThrow(
                    () -> new BaseException(BoardCommentErrorCode.EMPTY_BOARD_COMMENT));

                boardComment.increaseReport();
                if (boardComment.getReport().equals(REPORT_STANDARD)) {
                    boardComment.updateState();
                    sendReportEmail(boardComment.getMember().getEmail(), reportReq);
                }
            }
            case DISCUSSION_COMMENT -> {
                DiscussionComment discussionComment = discussionCommentRepository.findByIdAndStateIsTrue(
                    reportReq.getResourceId()).orElseThrow(
                    () -> new BaseException(DiscussionCommentErrorCode.EMPTY_DISCUSSION_COMMENT));

                discussionComment.increaseReport();
                if (discussionComment.getReport().equals(REPORT_STANDARD)) {
                    discussionComment.updateState();
                    sendReportEmail(discussionComment.getMember().getEmail(), reportReq);
                }
            }
            case MEMBER -> {
                Member targetMember = memberRepository.findByIdAndStatusIsTrue(
                        reportReq.getResourceId())
                    .orElseThrow(() -> new BaseException(MemberErrorCode.EMPTY_MEMBER));

                targetMember.increaseReport();
                if (targetMember.getReport().equals(REPORT_STANDARD)) {
                    targetMember.updateStatus();
                    sendReportEmail(targetMember.getEmail(), reportReq);
                }
            }
        }

        return "신고 완료";
    }

    // 누적 신고수가 10개가 넘는 경우 해당 유저에게 안내 메일 발송
    private void sendReportEmail(String email, ReportReq reportReq) throws MessagingException {
        MimeMessage mail = javaMailSender.createMimeMessage();
        int[] count = getReportCount(reportReq);

        // 메일 제목 지정
        mail.setSubject("[도와줘요, M쌤] 회원님의 신고 누적으로 인한 임시 정지/삭제에 관해 안내드립니다.", "utf-8");
        // 메일 내용 지정
        mail.setText(createReportEmailContent(reportReq.getReportTarget(), count), "utf-8", "html");

        // 파라미터로 받은 email을 전송할 email 주소로 설정
        mail.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        // 이메일 전송
        javaMailSender.send(mail);
    }

    // 각 신고 내용 별 개수 반환
    private int[] getReportCount(ReportReq reportReq) {
        int[] count = new int[REASON_SIZE];
        List<Report> reports = reportRepository.findByResourceIdAndReportTarget(
            reportReq.getResourceId(), reportReq.getReportTarget());

        ReportReason[] reasons = ReportReason.values();
        reports.forEach(report -> {
            for (int i = 0; i < count.length; i++) {
                if (report.getReportReason().equals(reasons[i])) {
                    count[i]++;
                    break;
                }
            }
        });
        return count;
    }

    // 이메일 내용에 신고 내역과 타입, 정지 or 삭제 단어 선택
    private String createReportEmailContent(ReportTarget reportTarget, int[] count) {
        String reportTargetName =
            reportTarget == ReportTarget.MEMBER ? "계정" : reportTarget.getName();
        String processType = reportTarget == ReportTarget.MEMBER ? "정지" : "삭제";
        String reportHistory = "";
        ReportReason[] reasons = ReportReason.values();
        for (int i = 0; i < count.length; i++) {
            reportHistory +=
                "<p style =\"line-height: 140%;\"><span\n style=\"font-size: 14px; line-height: 19.6px;\">"
                    + reasons[i].getName()
                    + " "
                    + count[i]
                    + "회</span>\n </p>\n";
        }

        return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional //EN\"\n"
            + "    \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
            + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
            + "<head>\n"
            + "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
            + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
            + "  <meta name=\"x-apple-disable-message-reformatting\">\n"
            + "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n"
            + "  <title></title>\n"
            + "  <style type=\"text/css\">\n"
            + "    @media only screen and (min-width: 620px) {\n"
            + "      .u-row {\n"
            + "        width: 600px !important;\n"
            + "      }\n"
            + "\n"
            + "      .u-row .u-col {\n"
            + "        vertical-align: top;\n"
            + "      }\n"
            + "\n"
            + "      .u-row .u-col-100 {\n"
            + "        width: 600px !important;\n"
            + "      }\n"
            + "    }\n"
            + "\n"
            + "    @media (max-width: 620px) {\n"
            + "      .u-row-container {\n"
            + "        max-width: 100% !important;\n"
            + "        padding-left: 0px !important;\n"
            + "        padding-right: 0px !important;\n"
            + "      }\n"
            + "\n"
            + "      .u-row .u-col {\n"
            + "        min-width: 320px !important;\n"
            + "        max-width: 100% !important;\n"
            + "        display: block !important;\n"
            + "      }\n"
            + "\n"
            + "      .u-row {\n"
            + "        width: 100% !important;\n"
            + "      }\n"
            + "\n"
            + "      .u-col {\n"
            + "        width: 100% !important;\n"
            + "      }\n"
            + "\n"
            + "      .u-col > div {\n"
            + "        margin: 0 auto;\n"
            + "      }\n"
            + "    }\n"
            + "\n"
            + "    body {\n"
            + "      margin: 0;\n"
            + "      padding: 0;\n"
            + "    }\n"
            + "\n"
            + "    table,\n"
            + "    tr,\n"
            + "    td {\n"
            + "      vertical-align: top;\n"
            + "      border-collapse: collapse;\n"
            + "    }\n"
            + "\n"
            + "    p {\n"
            + "      margin: 0;\n"
            + "    }\n"
            + "\n"
            + "    .ie-container table,\n"
            + "    .mso-container table {\n"
            + "      table-layout: fixed;\n"
            + "    }\n"
            + "\n"
            + "    * {\n"
            + "      line-height: inherit;\n"
            + "    }\n"
            + "\n"
            + "    a[x-apple-data-detectors='true'] {\n"
            + "      color: inherit !important;\n"
            + "      text-decoration: none !important;\n"
            + "    }\n"
            + "\n"
            + "    table, td {\n"
            + "      color: #000000;\n"
            + "    }\n"
            + "\n"
            + "    #u_body a {\n"
            + "      color: #161a39;\n"
            + "      text-decoration: underline;\n"
            + "    }\n"
            + "  </style>\n"
            + "  <link href=\"https://fonts.googleapis.com/css?family=Lato:400,700&display=swap\" rel=\"stylesheet\"\n"
            + "        type=\"text/css\">\n"
            + "</head>\n"
            + "<body class=\"clean-body u_body\"\n"
            + "      style=\"margin: 0;padding: 0;-webkit-text-size-adjust: 100%;background-color: #f9f9f9;color: #000000\">\n"
            + "<table id=\"u_body\"\n"
            + "       style=\"border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;min-width: 320px;Margin: 0 auto;background-color: #f9f9f9;width:100%\"\n"
            + "       cellpadding=\"0\" cellspacing=\"0\">\n"
            + "  <tbody>\n"
            + "  <tr style=\"vertical-align: top\">\n"
            + "    <td style=\"word-break: break-word;border-collapse: collapse !important;vertical-align: top\">\n"
            + "      <div class=\"u-row-container\" style=\"padding: 0px;background-color: #f9f9f9\">\n"
            + "        <div class=\"u-row\"\n"
            + "             style=\"margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #f9f9f9;\">\n"
            + "          <div\n"
            + "              style=\"border-collapse: collapse;display: table;width: 100%;height: 100%;background-color: transparent;\">\n"
            + "            <div class=\"u-col u-col-100\"\n"
            + "                 style=\"max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;\">\n"
            + "              <div style=\"height: 100%;width: 100% !important;\">\n"
            + "                <div\n"
            + "                    style=\"box-sizing: border-box; height: 100%; padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\">\n"
            + "                  <table style=\"font-family:'Lato',sans-serif;\" role=\"presentation\" cellpadding=\"0\"\n"
            + "                         cellspacing=\"0\" width=\"100%\" border=\"0\">\n"
            + "                    <tbody>\n"
            + "                    <tr>\n"
            + "                      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:15px;font-family:'Lato',sans-serif;\"\n"
            + "                          align=\"left\">\n"
            + "                        <table height=\"0px\" align=\"center\" border=\"0\" cellpadding=\"0\"\n"
            + "                               cellspacing=\"0\" width=\"100%\"\n"
            + "                               style=\"border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;border-top: 1px solid #f9f9f9;-ms-text-size-adjust: 100%;-webkit-text-size-adjust: 100%\">\n"
            + "                          <tbody>\n"
            + "                          <tr style=\"vertical-align: top\">\n"
            + "                            <td style=\"word-break: break-word;border-collapse: collapse !important;vertical-align: top;font-size: 0px;line-height: 0px;mso-line-height-rule: exactly;-ms-text-size-adjust: 100%;-webkit-text-size-adjust: 100%\">\n"
            + "                              <span>&#160;</span>\n"
            + "                            </td>\n"
            + "                          </tr>\n"
            + "                          </tbody>\n"
            + "                        </table>\n"
            + "                      </td>\n"
            + "                    </tr>\n"
            + "                    </tbody>\n"
            + "                  </table>\n"
            + "                </div>\n"
            + "              </div>\n"
            + "            </div>\n"
            + "          </div>\n"
            + "        </div>\n"
            + "      </div>\n"
            + "      <div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">\n"
            + "        <div class=\"u-row\"\n"
            + "             style=\"margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #ffffff;\">\n"
            + "          <div\n"
            + "              style=\"border-collapse: collapse;display: table;width: 100%;height: 100%;background-color: transparent;\">\n"
            + "            <div class=\"u-col u-col-100\"\n"
            + "                 style=\"max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;\">\n"
            + "              <div style=\"height: 100%;width: 100% !important;\">\n"
            + "                <div\n"
            + "                    style=\"box-sizing: border-box; height: 100%; padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\">\n"
            + "                  <table style=\"font-family:'Lato',sans-serif;\" role=\"presentation\" cellpadding=\"0\"\n"
            + "                         cellspacing=\"0\" width=\"100%\" border=\"0\">\n"
            + "                    <tbody>\n"
            + "                    <tr>\n"
            + "                      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:25px 10px;font-family:'Lato',sans-serif;\"\n"
            + "                          align=\"left\">\n"
            + "                        <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n"
            + "                          <tr>\n"
            + "                            <td style=\"padding-right: 0px;padding-left: 0px;\" align=\"center\">\n"
            + "                              <img align=\"center\" border=\"0\"\n"
            + "                                   src=\"https://assets.unlayer.com/stock-templates1690810526694-도와줘요엠쌤%20웹%20로고%201.png\"\n"
            + "                                   alt=\"Image\" title=\"Image\"\n"
            + "                                   style=\"outline: none;text-decoration: none;-ms-interpolation-mode: bicubic;clear: both;display: inline-block !important;border: none;height: auto;float: none;width: 37%;max-width: 214.6px;\"\n"
            + "                                   width=\"214.6\"/>\n"
            + "                            </td>\n"
            + "                          </tr>\n"
            + "                        </table>\n"
            + "                      </td>\n"
            + "                    </tr>\n"
            + "                    </tbody>\n"
            + "                  </table>\n"
            + "                </div>\n"
            + "              </div>\n"
            + "            </div>\n"
            + "          </div>\n"
            + "        </div>\n"
            + "      </div>\n"
            + "      <div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">\n"
            + "        <div class=\"u-row\"\n"
            + "             style=\"margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #161a39;\">\n"
            + "          <div\n"
            + "              style=\"border-collapse: collapse;display: table;width: 100%;height: 100%;background-color: transparent;\">\n"
            + "            <div class=\"u-col u-col-100\"\n"
            + "                 style=\"max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;\">\n"
            + "              <div style=\"background-color: #ad71ea;height: 100%;width: 100% !important;\">\n"
            + "                <div\n"
            + "                    style=\"box-sizing: border-box; height: 100%; padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\">\n"
            + "                  <table style=\"font-family:'Lato',sans-serif;\" role=\"presentation\" cellpadding=\"0\"\n"
            + "                         cellspacing=\"0\" width=\"100%\" border=\"0\">\n"
            + "                    <tbody>\n"
            + "                    <tr>\n"
            + "                      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:0px 10px 30px;font-family:'Lato',sans-serif;\"\n"
            + "                          align=\"left\">\n"
            + "                        <div\n"
            + "                            style=\"font-size: 14px; line-height: 140%; text-align: left; word-wrap: break-word;\">\n"
            + "                          <p style=\"font-size: 14px; line-height: 140%; text-align: center;\"> </p>\n"
            + "                          <p style=\"font-size: 14px; line-height: 140%; text-align: center;\"><span\n"
            + "                              style=\"font-size: 22px; line-height: 30.8px; color: #ffffff; font-family: Lato, sans-serif;\">신고 누적으로 인한 임시 삭제 안내</span>\n"
            + "                          </p>\n"
            + "                        </div>\n"
            + "\n"
            + "                      </td>\n"
            + "                    </tr>\n"
            + "                    </tbody>\n"
            + "                  </table>\n"
            + "                </div>\n"
            + "              </div>\n"
            + "            </div>\n"
            + "          </div>\n"
            + "        </div>\n"
            + "      </div>\n"
            + "      <div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">\n"
            + "        <div class=\"u-row\"\n"
            + "             style=\"margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #ffffff;\">\n"
            + "          <div\n"
            + "              style=\"border-collapse: collapse;display: table;width: 100%;height: 100%;background-color: transparent;\">\n"
            + "            <div class=\"u-col u-col-100\"\n"
            + "                 style=\"max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;\">\n"
            + "              <div style=\"height: 100%;width: 100% !important;\">\n"
            + "                <div\n"
            + "                    style=\"box-sizing: border-box; height: 100%; padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\">\n"
            + "                  <table style=\"font-family:'Lato',sans-serif;\" role=\"presentation\" cellpadding=\"0\"\n"
            + "                         cellspacing=\"0\" width=\"100%\" border=\"0\">\n"
            + "                    <tbody>\n"
            + "                    <tr>\n"
            + "                      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:40px 40px 30px;font-family:'Lato',sans-serif;\"\n"
            + "                          align=\"left\">\n"
            + "                        <div\n"
            + "                            style=\"font-size: 14px; line-height: 140%; text-align: left; word-wrap: break-word;\">\n"
            + "                          <p style=\"line-height: 140%;\"><span\n"
            + "                              style=\"font-size: 16px; line-height: 22.4px;\">안녕하세요. 도와줘요 M쌤입니다.</span>\n"
            + "                          </p>\n"
            + "                          <p style=\"line-height: 140%;\"> </p>\n"
            + "                          <p style=\"line-height: 140%;\"><span\n"
            + "                              style=\"font-size: 16px; line-height: 22.4px;\">회원님의 "
            + reportTargetName + "이 누적 신고 10회 이상으로 </span><span\n"
            + "                              style=\"font-size: 16px; line-height: 22.4px;\">임시 "
            + processType + "되었음을 알려드립니다.</span>\n"
            + "                          </p>\n"
            + "                          <p style=\"line-height: 140%;\"> </p>\n"
            + "                          <p style=\"line-height: 140%;\"><span\n"
            + "                              style=\"font-size: 16px; line-height: 22.4px;\">신고 내역</span></p>\n"
            + reportHistory
            + "                          <p style=\"line-height: 140%;\"> </p>\n"
            + "                          <p style=\"line-height: 140%;\"><span\n"
            + "                              style=\"font-size: 16px; line-height: 22.4px;\">문의사항은 도와줘요 M쌤의 이메일을 통해 연락 바랍니다.</span>\n"
            + "                          </p>\n"
            + "                          <p style=\"line-height: 140%;\"><span\n"
            + "                              style=\"font-size: 16px; line-height: 22.4px;\">도와줘요 M쌤을 이용해주셔서 감사합니다. </span>\n"
            + "                          </p>\n"
            + "                        </div>\n"
            + "                      </td>\n"
            + "                    </tr>\n"
            + "                    </tbody>\n"
            + "                  </table>\n"
            + "                </div>\n"
            + "              </div>\n"
            + "            </div>\n"
            + "          </div>\n"
            + "        </div>\n"
            + "      </div>\n"
            + "      <div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">\n"
            + "        <div class=\"u-row\"\n"
            + "             style=\"margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #18163a;\">\n"
            + "          <div\n"
            + "              style=\"border-collapse: collapse;display: table;width: 100%;height: 100%;background-color: transparent;\">\n"
            + "            <div class=\"u-col u-col-100\"\n"
            + "                 style=\"max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;\">\n"
            + "              <div style=\"background-color: #ad71ea;height: 100%;width: 100% !important;\">\n"
            + "                <div\n"
            + "                    style=\"box-sizing: border-box; height: 100%; padding: 20px 20px 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\">\n"
            + "                  <table style=\"font-family:'Lato',sans-serif;\" role=\"presentation\" cellpadding=\"0\"\n"
            + "                         cellspacing=\"0\" width=\"100%\" border=\"0\">\n"
            + "                    <tbody>\n"
            + "                    <tr>\n"
            + "                      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:10px;font-family:'Lato',sans-serif;\"\n"
            + "                          align=\"left\">\n"
            + "                        <div\n"
            + "                            style=\"font-size: 14px; line-height: 140%; text-align: left; word-wrap: break-word;\">\n"
            + "                          <p style=\"font-size: 14px; line-height: 140%;\"><span\n"
            + "                              style=\"font-size: 16px; line-height: 22.4px; color: #ffffff;\">Contact</span>\n"
            + "                          </p>\n"
            + "                          <p style=\"font-size: 14px; line-height: 140%;\"><span\n"
            + "                              style=\"color: #ffffff; line-height: 19.6px;\"><a rel=\"noopener\"\n"
            + "                                                                              href=\"mailto:help.mssaem@gmail.com\"\n"
            + "                                                                              target=\"_blank\"\n"
            + "                                                                              style=\"color: #ffffff;\">help.mssaem@gmail.com</a></span>\n"
            + "                          </p>\n"
            + "                          <p style=\"font-size: 14px; line-height: 140%;\"> </p>\n"
            + "                        </div>\n"
            + "                      </td>\n"
            + "                    </tr>\n"
            + "                    </tbody>\n"
            + "                  </table>\n"
            + "                </div>\n"
            + "              </div>\n"
            + "            </div>\n"
            + "          </div>\n"
            + "        </div>\n"
            + "      </div>\n"
            + "    </td>\n"
            + "  </tr>\n"
            + "  </tbody>\n"
            + "</table>\n"
            + "</body>\n"
            + "\n"
            + "</html>\n";
    }
}