package com.example.mssaem_backend.global.config.exception.errorCode;

import com.example.mssaem_backend.global.config.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DiscussionCommentErrorCode implements ErrorCode {

    INVALID_MEMBER("DISCUSSION_COMMENT_001", "토론글 댓글을 수정할 권한이 없습니다.", HttpStatus.UNAUTHORIZED),
    EMPTY_DISCUSSION_COMMENT("DISCUSSION_COMMENT_002","토론글 댓글이 존재하지 않습니다." , HttpStatus.NOT_FOUND);
    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
