package com.example.mssaem_backend.global.common;

import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommonController {

    @GetMapping("/access/denied")
    public String accessDenied() {
        throw new BaseException(GlobalErrorCode.ACCESS_DENIED);
    }
}
