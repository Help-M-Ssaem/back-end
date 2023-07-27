package com.example.mssaem_backend.global.common;

import com.example.mssaem_backend.domain.member.Member;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.MemberErrorCode;

public class CheckWriter {

    //현재 viewer와 작성자가 같지 않으면 예외 처리
    public static void match(Member viewer, Member writer) {
        if( (viewer == null) || !(viewer.getId().equals(writer.getId()))) {
            throw new BaseException(MemberErrorCode.INVALID_MEMBER);
        }
    }
    //현재 viewer와 작성자가 같으면 수정/삭제 권한 부여(true 리턴)
    public static boolean isMatch(Member viewer, Member writer) {
        return (viewer != null) && (viewer.getId().equals(writer.getId()));
    }
}
