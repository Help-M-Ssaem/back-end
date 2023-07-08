package com.example.mssaem_backend.evaluation;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EvaluationEnum implements EnumMapperType {
    LIKE("좋아요"), USEFUL("유익해요"), FUN("재밌어요"), SINCERE("성의있어요"), HOT("화끈해요")
    ;
    private final String title;

    @Override
    public String getTitle() {
        return title;
    }
}
