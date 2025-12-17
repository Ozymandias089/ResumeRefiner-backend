package com.resumerefiner.resumerefinerbackend.media.application.ports.out;

import java.io.InputStream;

public interface MediaStorage {
    /**
     * 공개 접근 가능한 URL을 반환한다고 가정
     */
    String uploadPublic(String key, String contentType, InputStream inputStream, long sizeBytes);
}
