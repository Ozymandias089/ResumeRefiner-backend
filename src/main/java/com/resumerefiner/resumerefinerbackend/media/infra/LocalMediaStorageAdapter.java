package com.resumerefiner.resumerefinerbackend.media.infra;

import com.resumerefiner.resumerefinerbackend.media.application.ports.out.MediaStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalMediaStorageAdapter implements MediaStorage {

    @Value("${app.storage.local.root-dir}")
    private String rootDir;

    @Value("${app.storage.local.public-base-url}")
    private String publicBaseUrl;

    @Override
    public String uploadPublic(String key, String contentType, InputStream inputStream, long sizeBytes) {
        // key: profile/{memberId}/{uuid}.png
        Path target = Paths.get(rootDir).resolve(key).normalize();

        try {
            Files.createDirectories(target.getParent());
            // 덮어쓰기 허용
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("failed to store file locally", e);
        }

        // 로컬 접근 URL: http://localhost:8080/static/profile/.. 형태로 매핑할 예정
        String base = publicBaseUrl.replaceAll("/$", "");
        return base + "/static/" + key;
    }
}
