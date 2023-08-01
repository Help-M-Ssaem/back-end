package com.example.mssaem_backend.domain.report;

import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.domain.report.dto.ReportRequestDto.ReportReq;
import com.example.mssaem_backend.global.config.security.auth.CurrentMember;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/member/reports")
    public ResponseEntity<String> report(@CurrentMember Member member,@RequestBody ReportReq reportReq)
        throws MessagingException {
        return ResponseEntity.ok(reportService.report(member, reportReq));
    }

}
