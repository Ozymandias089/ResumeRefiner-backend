package com.resumerefiner.resumerefinerbackend.resume.domain.vo;

public enum MilitaryStatus {
    NOT_APPLICABLE, // 대상 아님(예: 여성/외국인 등) - UX에서 "해당 없음"
    NOT_SERVED,     // 미필
    SERVING,        // 복무중
    SERVED,         // 군필
    EXEMPT          // 면제
}
