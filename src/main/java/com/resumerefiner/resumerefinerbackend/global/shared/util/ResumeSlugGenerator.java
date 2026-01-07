package com.resumerefiner.resumerefinerbackend.global.shared.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * NOTE:
 * This class is intentionally framework-agnostic.
 * Planned to be extracted as a standalone library after v1.0 release.
 */
public final class ResumeSlugGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String ADJ_PATH = "slug/adjectives-en.txt";
    private static final String NOUN_PATH = "slug/nouns-en.txt";

    private static final String DELIMITER = "-";
    private static final int SUFFIX_LEN = 4;

    // 슬러그 단어는 안전하게 (소문자 알파벳만)
    private static final Pattern WORD_PATTERN = Pattern.compile("^[a-z]{2,30}$");

    // lazy cache
    private static volatile List<String> ADJECTIVES;
    private static volatile List<String> NOUNS;

    private ResumeSlugGenerator() {}

    /** 기본: adj-adj-noun-suffix */
    public static String generate() {
        ensureLoaded();

        String base =
                pick(ADJECTIVES) + DELIMITER +
                        pick(ADJECTIVES) + DELIMITER +
                        pick(NOUNS);

        return base + DELIMITER + randomBase36(SUFFIX_LEN);
    }

    private static void ensureLoaded() {
        if (ADJECTIVES != null && NOUNS != null) return;

        synchronized (ResumeSlugGenerator.class) {
            if (ADJECTIVES == null) ADJECTIVES = loadWords(ADJ_PATH);
            if (NOUNS == null) NOUNS = loadWords(NOUN_PATH);

            if (ADJECTIVES.isEmpty()) throw new IllegalStateException("adjectives list is empty");
            if (NOUNS.isEmpty()) throw new IllegalStateException("nouns list is empty");
        }
    }

    private static List<String> loadWords(String path) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) cl = ResumeSlugGenerator.class.getClassLoader();

        InputStream is = cl.getResourceAsStream(path);
        if (is == null) throw new IllegalStateException("word list not found: " + path);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            List<String> out = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String w = line.trim().toLowerCase();
                if (w.isEmpty() || w.startsWith("#")) continue;
                if (!WORD_PATTERN.matcher(w).matches()) continue;
                out.add(w);
            }
            return out.stream().distinct().toList();
        } catch (Exception e) {
            throw new IllegalStateException("failed to load word list: " + path, e);
        }
    }

    private static String pick(List<String> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }

    private static String randomBase36(int len) {
        char[] out = new char[len];
        for (int i = 0; i < len; i++) {
            int v = RANDOM.nextInt(36);
            out[i] = (char) (v < 10 ? ('0' + v) : ('a' + (v - 10)));
        }
        return new String(out);
    }
}
