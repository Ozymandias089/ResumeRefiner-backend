package com.resumerefiner.resumerefinerbackend.media.infra;

import com.resumerefiner.resumerefinerbackend.media.application.ports.out.MediaStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

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

    @Override
    public void delete(String key) {
        if (key == null || key.isBlank()) return;

        try {
            DeleteObjectRequest req = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(req);

            // S3 deleteObject는 기본적으로 "없어도 성공"에 가깝게 동작(멱등).
        } catch (S3Exception e) {
            // 운영에서는 delete 실패를 어느 정도 무시할지 정책이 필요함.
            // 교체/삭제 중 S3 delete만 실패했다고 전체 트랜잭션을 깨고 싶지 않다면 여기서 로깅만 하고 return.
            // 너는 현재 예외 정책이 "문제면 터뜨린다"에 가까우니 일단 런타임으로 올려도 됨.
            throw new IllegalStateException("failed to delete object from s3. key=" + key, e);
        }
    }
}

