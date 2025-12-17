package com.resumerefiner.resumerefinerbackend.media.domain;

import java.util.List;
import java.util.Optional;

public interface MediaFileRepository {

    MediaFile save(MediaFile mediaFile);

    Optional<MediaFile> findById(Long id);

    Optional<String> findUrlById(Long id);

    List<MediaFile> findByOwner(MediaOwnerType ownerType, Long ownerId);
}
