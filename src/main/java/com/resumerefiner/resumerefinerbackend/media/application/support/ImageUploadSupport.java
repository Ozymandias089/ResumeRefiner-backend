package com.resumerefiner.resumerefinerbackend.media.application.support;

import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.EmptyFileException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.FileTooLargeException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.UnsupportedMediaTypeException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.Set;

public final class ImageUploadSupport {

    public static final long DEFAULT_MAX_BYTES = 5L * 1024 * 1024;
    public static final Set<String> DEFAULT_ALLOWED_CT = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    private ImageUploadSupport() {}

    public static void validateImage(MultipartFile file, long maxBytes, Set<String> allowedCt) {
        if (file == null || file.isEmpty()) throw new EmptyFileException();
        if (file.getSize() > maxBytes) throw new FileTooLargeException(maxBytes);

        String ct = normalizeContentType(file.getContentType());
        if (!allowedCt.contains(ct)) throw new UnsupportedMediaTypeException(ct, allowedCt);
    }

    public static String normalizeContentType(String ct) {
        if (ct == null) return "";
        if (ct.equalsIgnoreCase("image/jpg")) return "image/jpeg";
        return ct.toLowerCase(Locale.ROOT);
    }

    public static String contentTypeToExt(String ct) {
        return switch (ct) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> throw new UnsupportedMediaTypeException(ct, DEFAULT_ALLOWED_CT);
        };
    }

    public static String defaultFileName(String original, String prefix, String ext) {
        if (original == null || original.isBlank()) return prefix + "." + ext;
        return original;
    }
}
