package com.resumerefiner.resumerefinerbackend.media.application.support;

import java.util.UUID;

public final class StorageKeyFactory {
    private StorageKeyFactory() {}

    public static String key(String prefix, Long ownerId, String ext) {
        return "%s/%d/%s.%s".formatted(prefix, ownerId, UUID.randomUUID(), ext);
    }
}
