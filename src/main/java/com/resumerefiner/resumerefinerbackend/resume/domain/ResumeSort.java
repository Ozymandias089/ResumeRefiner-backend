package com.resumerefiner.resumerefinerbackend.resume.domain;

import org.springframework.data.domain.Sort;

public enum ResumeSort {
    UPDATED_AT_DESC,
    UPDATED_AT_ASC,
    CREATED_AT_DESC,
    CREATED_AT_ASC,
    TITLE_ASC,
    TITLE_DESC;

    public static Sort toSort(ResumeSort sort) {
        return switch (sort) {
            case UPDATED_AT_DESC -> Sort.by(Sort.Direction.DESC, "updatedAt");
            case UPDATED_AT_ASC  -> Sort.by(Sort.Direction.ASC, "updatedAt");
            case CREATED_AT_DESC -> Sort.by(Sort.Direction.DESC, "createdAt");
            case CREATED_AT_ASC  -> Sort.by(Sort.Direction.ASC, "createdAt");
            case TITLE_ASC       -> Sort.by(Sort.Direction.ASC, "title");
            case TITLE_DESC      -> Sort.by(Sort.Direction.DESC, "title");
        };
    }
}