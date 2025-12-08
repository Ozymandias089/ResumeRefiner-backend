package com.resumerefiner.resumerefinerbackend.media.infra;

import com.resumerefiner.resumerefinerbackend.media.domain.MediaFile;
import com.resumerefiner.resumerefinerbackend.media.domain.MediaFileRepository;
import com.resumerefiner.resumerefinerbackend.media.domain.MediaOwnerType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MediaFileRepositoryAdapter implements MediaFileRepository {

    private final MediaFileJpaRepository jpa;

    @Override
    public MediaFile save(MediaFile mediaFile) {
        return jpa.save(mediaFile);
    }

    @Override
    public Optional<MediaFile> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<MediaFile> findByOwner(MediaOwnerType ownerType, Long ownerId) {
        return jpa.findByOwnerTypeAndOwnerId(ownerType, ownerId);
    }
}
