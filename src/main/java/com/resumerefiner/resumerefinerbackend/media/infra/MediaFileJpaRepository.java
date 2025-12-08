package com.resumerefiner.resumerefinerbackend.media.infra;

import com.resumerefiner.resumerefinerbackend.media.domain.MediaFile;
import com.resumerefiner.resumerefinerbackend.media.domain.MediaOwnerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaFileJpaRepository extends JpaRepository<MediaFile, Long> {

    List<MediaFile> findByOwnerTypeAndOwnerId(MediaOwnerType ownerType, Long ownerId);
}
