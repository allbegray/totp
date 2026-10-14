package io.github.allbegray.totp;

import org.jspecify.annotations.Nullable;
import java.time.Instant;

public final class TotpVerifier {
    private final TotpGenerator generator;
    private final int window;

    public TotpVerifier() {
        this(new TotpGenerator(), 1);
    }

    public TotpVerifier(TotpGenerator generator) {
        this(generator, 1);
    }

    public TotpVerifier(TotpGenerator generator, int window) {
        if (generator == null) {
            throw new IllegalArgumentException("generator must not be null");
        }
        if (window < 0) {
            throw new IllegalArgumentException("window must not be negative");
        }
        this.generator = generator;
        this.window = window;
    }

    public boolean verify(@Nullable String code, @Nullable String secret) {
        return verify(code, secret, Instant.now());
    }

    public boolean verify(@Nullable String code, @Nullable String secret, @Nullable Instant time) {
        return verify(code, secret, time, window);
    }

    public boolean verify(@Nullable String code, @Nullable String secret, @Nullable Instant time, int customWindow) {
        if (code == null || secret == null || time == null) {
            return false;
        }
        if (customWindow < 0) {
            return false;
        }
        String normalized = normalize(code);
        if (normalized.length() != generator.digits()) {
            return false;
        }
        if (!normalized.chars().allMatch(Character::isDigit)) {
            return false;
        }
        long counter = Math.floorDiv(time.getEpochSecond(), generator.periodSeconds());
        int low = (int) -customWindow;
        int high = customWindow;
        for (int i = low; i <= high; i++) {
            long candidate = counter + i;
            if (candidate < 0) {
                continue;
            }
            String expected = generator.generate(secret, candidate);
            if (constantTimeEquals(expected, normalized)) {
                return true;
            }
        }
        return false;
    }

    private static String normalize(String code) {
        return code.trim().replace(" ", "");
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}