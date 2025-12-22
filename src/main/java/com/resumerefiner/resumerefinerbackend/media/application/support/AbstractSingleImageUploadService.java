package com.resumerefiner.resumerefinerbackend.media.application.support;

import com.resumerefiner.resumerefinerbackend.media.application.ports.out.MediaStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public abstract class AbstractSingleImageUploadService {

    protected final MediaStorage mediaStorage;

    protected AbstractSingleImageUploadService(MediaStorage mediaStorage) {
        this.mediaStorage = mediaStorage;
    }

    protected record Uploaded(String storageKey, String url, String contentType, long sizeBytes, String originalName) {}

    protected Uploaded uploadToStorage(String prefix, Long ownerId, MultipartFile file) {
        ImageUploadSupport.validateImage(file, ImageUploadSupport.DEFAULT_MAX_BYTES, ImageUploadSupport.DEFAULT_ALLOWED_CT);

        String contentType = ImageUploadSupport.normalizeContentType(file.getContentType());
        String ext = ImageUploadSupport.contentTypeToExt(contentType);

        String key = StorageKeyFactory.key(prefix, ownerId, ext);

        final String url;
        try (InputStream in = file.getInputStream()) {
            url = mediaStorage.uploadPublic(key, contentType, in, file.getSize());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to upload file: " + e.getMessage(), e);
        }

        String originalName = ImageUploadSupport.defaultFileName(file.getOriginalFilename(), prefix, ext);

        return new Uploaded(key, url, contentType, file.getSize(), originalName);
    }

    protected void deleteFromStorageQuietly(String storageKey) {
        if (storageKey == null || storageKey.isBlank()) return;
        try {
            mediaStorage.delete(storageKey);
        } catch (RuntimeException ex) {
            log.warn("Failed to delete from storage. key={}, msg={}", storageKey, ex.getMessage());
        }
    }
}
