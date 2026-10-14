package io.github.allbegray.totp;

import org.jspecify.annotations.Nullable;
import java.time.Instant;

/** TotpVerifier. */
public final class TotpVerifier {
    private final TotpGenerator generator;
    private final int window;
    /**
     * TotpVerifier.*
     */

    public TotpVerifier() {
        this(new TotpGenerator(), 1);
    }
    /**
     * TotpVerifier.*
     *
     * @param generator the generator
     */

    public TotpVerifier(TotpGenerator generator) {
        this(generator, 1);
    }
    /**
     * TotpVerifier.*
     *
     * @param generator the generator
     * @param window the window
     */

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
    /**
     * verify.*
     *
     * @param code the code
     * @param secret the secret
     * @return the result
     */

    public boolean verify(@Nullable String code, @Nullable String secret) {
        return verify(code, secret, Instant.now());
    }
    /**
     * verify.*
     *
     * @param code the code
     * @param secret the secret
     * @param time the time
     * @return the result
     */

    public boolean verify(@Nullable String code, @Nullable String secret, @Nullable Instant time) {
        return verify(code, secret, time, window);
    }
    /**
     * verify.*
     *
     * @param code the code
     * @param secret the secret
     * @param time the time
     * @param customWindow the customWindow
     * @return the result
     */

    public boolean verify(@Nullable String code, @Nullable String secret, @Nullable Instant time, int customWindow) {
        return verifyAndGetTimeStep(code, secret, time, customWindow).valid();
    }

    /**
     * Verifies a code and returns the matched time step when successful.
     *
     * @param code          the supplied code
     * @param secret        the Base32-encoded secret
     * @param time          the time to verify against
     * @param customWindow  the number of accepted steps before and after {@code time}
     * @return the verification result
     */
    public TotpVerificationResult verifyAndGetTimeStep(@Nullable String code, @Nullable String secret, @Nullable Instant time, int customWindow) {
        if (code == null || secret == null || time == null || customWindow < 0) {
            return TotpVerificationResult.invalid();
        }
        String normalized = normalize(code);
        if (normalized.length() != generator.digits() || !normalized.chars().allMatch(Character::isDigit)) {
            return TotpVerificationResult.invalid();
        }
        long counter = Math.floorDiv(time.getEpochSecond(), generator.periodSeconds());
        for (int i = -customWindow; i <= customWindow; i++) {
            long candidate = counter + i;
            if (candidate < 0) {
                continue;
            }
            String expected = generator.generate(secret, candidate);
            if (constantTimeEquals(expected, normalized)) {
                return new TotpVerificationResult(true, candidate);
            }
        }
        return TotpVerificationResult.invalid();
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