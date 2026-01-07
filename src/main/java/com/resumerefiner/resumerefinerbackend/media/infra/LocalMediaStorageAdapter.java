package com.resumerefiner.resumerefinerbackend.media.infra;

import com.resumerefiner.resumerefinerbackend.media.application.ports.out.MediaStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
@RequiredArgsConstructor
public class LocalMediaStorageAdapter implements MediaStorage {

    @Value("${app.storage.local.root-dir}")
    private String rootDir;

    @Value("${app.storage.local.public-base-url}")
    private String publicBaseUrl;

    @Override
    public String uploadPublic(String key, String contentType, InputStream inputStream, long sizeBytes) {
        Path target = Paths.get(rootDir).resolve(key).normalize();

        // rootDir 밖으로 탈출 방지 (업로드에도 적용하는 게 좋아)
        Path root = Paths.get(rootDir).toAbsolutePath().normalize();
        Path absTarget = target.toAbsolutePath().normalize();
        if (!absTarget.startsWith(root)) {
            throw new IllegalArgumentException("invalid storage key: " + key);
        }

        try {
            Files.createDirectories(target.getParent());
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("failed to store file locally", e);
        }

        String base = publicBaseUrl.replaceAll("/$", "");
        return base + "/static/" + key;
    }

    @Override
    public void delete(String key) {
        if (key == null || key.isBlank()) return;

        Path target = Paths.get(rootDir).resolve(key).normalize();

        Path root = Paths.get(rootDir).toAbsolutePath().normalize();
        Path absTarget = target.toAbsolutePath().normalize();
        if (!absTarget.startsWith(root)) {
            throw new IllegalArgumentException("invalid storage key: " + key);
        }

        try {
            Files.deleteIfExists(absTarget);
        } catch (IOException e) {
            throw new IllegalStateException("failed to delete file locally", e);
        }
    }
}
