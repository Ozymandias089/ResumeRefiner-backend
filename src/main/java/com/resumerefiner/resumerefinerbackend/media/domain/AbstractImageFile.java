package com.resumerefiner.resumerefinerbackend.media.domain;

import com.resumerefiner.resumerefinerbackend.global.jpa.BaseTimeEntity;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.domain.DomainRuleViolationException;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractImageFile extends BaseTimeEntity {

    @Getter
    @Column(name = "storage_key", nullable = false, length = 512)
    protected String storageKey;

    @Getter
    @Column(name = "url", nullable = false, length = 512)
    protected String url;

    @Getter
    @Column(name = "file_name", nullable = false, length = 255)
    protected String fileName;

    @Getter
    @Column(name = "content_type", nullable = false, length = 100)
    protected String contentType;

    @Getter
    @Column(name = "size_bytes", nullable = false)
    protected long sizeBytes;

    protected AbstractImageFile(String storageKey, String url, String fileName, String contentType, long sizeBytes) {
        if (storageKey == null || storageKey.isBlank()) throw new DomainRuleViolationException("storageKey must not be blank");
        if (url == null || url.isBlank()) throw new DomainRuleViolationException("url must not be blank");
        if (fileName == null || fileName.isBlank()) throw new DomainRuleViolationException("fileName must not be blank");
        if (contentType == null || contentType.isBlank()) throw new DomainRuleViolationException("contentType must not be blank");
        if (sizeBytes < 0) throw new DomainRuleViolationException("sizeBytes must be >= 0");

        this.storageKey = storageKey;
        this.url = url;
        this.fileName = fileName;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
    }

    public void changeUrl(String newUrl) {
        if (newUrl == null || newUrl.isBlank()) throw new DomainRuleViolationException("url must not be blank");
        this.url = newUrl;
    }
}
