package com.example.mssaem_backend.global.s3.dto;

import lombok.Getter;

@Getter
public class S3Result {
    private String imgUrl;

    public S3Result(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
