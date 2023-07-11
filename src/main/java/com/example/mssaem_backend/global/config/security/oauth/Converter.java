package com.example.mssaem_backend.global.config.security.oauth;

import org.springframework.context.annotation.Configuration;

@Configuration
public class Converter implements org.springframework.core.convert.converter.Converter<String, SocialLoginType> {

    @Override
    public SocialLoginType convert(String source) {
        return SocialLoginType.valueOf(source.toUpperCase());
    }
}
