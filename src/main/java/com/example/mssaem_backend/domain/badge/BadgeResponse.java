package com.example.mssaem_backend.domain.badge;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BadgeResponse {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BadgeInfo {
        private Long id;
        private String name;
    }
}
