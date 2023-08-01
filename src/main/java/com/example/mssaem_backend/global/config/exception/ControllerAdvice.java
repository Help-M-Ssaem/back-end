package com.example.mssaem_backend.global.config.exception;

import com.example.mssaem_backend.global.config.exception.errorCode.GlobalErrorCode;
import com.example.mssaem_backend.global.config.exception.errorCode.ReportError;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

@RestControllerAdvice
public class ControllerAdvice {

    /**
     * 미리 지정해놓은 에러 e 발생 시 ExceptionResponse 로 반환
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionResponse> handleBaseException(BaseException e) {
        return new ResponseEntity<>(new ExceptionResponse(e.getErrorCode(), e.getMessage()), e.getStatus());
    }

    /**
     * CASE: 잘못된 URI 요청
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNoHandlerFoundException(
            NoHandlerFoundException e) {
        System.out.println(e);
        return convert(GlobalErrorCode.NOT_SUPPORTED_URI_ERROR, HttpStatus.NOT_FOUND);
    }

    /**
     * CASE: 잘못된 HTTP METHOD 요청
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        System.out.println(e);
        return convert(GlobalErrorCode.NOT_SUPPORTED_METHOD_ERROR, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * CASE: 잘못된 MEDIA TYPE 요청
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException e) {
        System.out.println(e);
        return convert(GlobalErrorCode.NOT_SUPPORTED_MEDIA_TYPE_ERROR,
                HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * CASE: 서버 내부 에러
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeException(RuntimeException e) {
        System.out.println(e);
        return convert(GlobalErrorCode.SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * CASE: 잘못된 ARGUMENT 요청
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException e) {
        System.out.println(e);
        String detailMessage = extractMessage(e.getBindingResult().getFieldErrors());
        return convert(GlobalErrorCode.NOT_VALID_ARGUMENT_ERROR, detailMessage);
    }

    /**
     * CASE: 메일 관련 에러
     */
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException e) {
        System.out.println(e);
        return convert(ReportError.FAIL_SEND_MAIL, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String extractMessage(List<FieldError> fieldErrors) {
        StringBuilder builder = new StringBuilder();
        fieldErrors.forEach((error) -> builder.append(error.getDefaultMessage()));
        return builder.toString();
    }

    private ResponseEntity<ExceptionResponse> convert(ErrorCode e, HttpStatus httpStatus) {
        return new ResponseEntity<>(new ExceptionResponse(e.getErrorCode(), e.getMessage()), httpStatus);
    }

    private ResponseEntity<ExceptionResponse> convert(ErrorCode e, String detailMessage) {
        ExceptionResponse exceptionRes = new ExceptionResponse(e.getErrorCode(), detailMessage);
        return new ResponseEntity<>(exceptionRes, HttpStatus.BAD_REQUEST);
    }

}
