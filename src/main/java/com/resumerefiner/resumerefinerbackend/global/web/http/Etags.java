package com.resumerefiner.resumerefinerbackend.global.web.http;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Etags {

    // 서버 → 클라이언트
    public static String fromVersion(long v) {
        return "\"" + v + "\""; // → "3"
    }

    // 클라이언트 → 서버
    public static Long parseIfMatch(String ifMatch) {
        if (ifMatch == null || ifMatch.isBlank()) return null;

        String s = ifMatch.trim();
        if (s.equals("*")) return null; // 우리는 * 허용 안 할 예정

        if (s.startsWith("W/")) s = s.substring(2).trim(); // weak etag 허용 시
        if (s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length() - 1);
        }

        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
