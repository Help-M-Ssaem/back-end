package com.example.mssaem_backend.domain.badge;

import lombok.Getter;

@Getter
public enum BadgeEnum {
    MBTIRANO("엠비티라노", "화끈해요", 10, "https://mssaem-bucket.s3.ap-northeast-2.amazonaws.com/M-ssaem/6499e7c2-3cfe-45f4-ba8c-75abacaece15.png"),
    MBTADULT("엠비티어른", "유익해요", 10, "https://mssaem-bucket.s3.ap-northeast-2.amazonaws.com/M-ssaem/9c3a46ff-ac79-47ad-be55-371366d2a6f0.png"),
    MBTMI("MBTMI", "성의있어요", 10, "https://mssaem-bucket.s3.ap-northeast-2.amazonaws.com/M-ssaem/dd19d446-dbc0-4a56-b7f7-be0d25fad439.png"),
    FUNFUN("FUNFUN", "재밌어요", 10, "https://mssaem-bucket.s3.ap-northeast-2.amazonaws.com/M-ssaem/af60b0a3-daba-4437-8680-e0d8ed7a2048.png"),
    NEWBIE("NEWBIE", "뉴비",0, null);

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
