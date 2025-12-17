package com.resumerefiner.resumerefinerbackend.media.infra;

import com.resumerefiner.resumerefinerbackend.media.application.ports.out.MediaStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

@Component
@Profile("prod")
@RequiredArgsConstructor
public class S3MediaStorageAdapter implements MediaStorage {

    private final S3Client s3Client;

    @Value("${app.storage.s3.bucket}")
    private String bucket;

    @Value("${app.storage.s3.region}")
    private String region;

    @Value("${app.storage.s3.public-base-url}")
    private String publicBaseUrl;

    @Override
    public String uploadPublic(String key, String contentType, InputStream inputStream, long sizeBytes) {

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(req, RequestBody.fromInputStream(inputStream, sizeBytes));

        // CDN base url 사용 (예: https://cdn.resumerefiner.app)
        String base = publicBaseUrl.replaceAll("/$", "");
        return base + "/" + key;
    }
}

