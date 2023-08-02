package com.example.mssaem_backend.global.config.exception.errorCode;

import com.example.mssaem_backend.global.config.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReportError implements ErrorCode {

    DUPLICATE_REPORT("REPORT_001", "일주일 내에 신고한 이력이 있습니다.", HttpStatus.CONFLICT),
    FAIL_SEND_MAIL("REPORT_002", "메일 전송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
