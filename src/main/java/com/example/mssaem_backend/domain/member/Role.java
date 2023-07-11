package com.example.mssaem_backend.domain.member;

public enum Role {
    ROLE_MEMBER("로그인 회원"),
    ROLE_MANAGER("관리자");

    private final String name;

    Role(String name) {
        this.name = name;
    }
}
