package com.example.mssaem_backend.domain.badge;

import lombok.Getter;

@Getter
public enum BadgeEnum {
    MBTIRANO("엠비티라노", "화끈해요", 10, ""),
    MBTADULT("엠비티어른", "유익해요", 10, ""),
    MBTMI("MBTMI", "성의있어요", 10, ""),
    FUNFUN("FUNFUN", "재밌어요", 10, "");

    private final String name;
    private final String evaluation;
    private final int standard;
    private final String url;

    public int getStandard(){
        return standard;
    }

    BadgeEnum(String name, String evaluation, int standard, String url) {
        this.name = name;
        this.evaluation = evaluation;
        this.standard = standard;
        this.url = url;
    }

}
