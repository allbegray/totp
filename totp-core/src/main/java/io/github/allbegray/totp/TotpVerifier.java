package io.github.allbegray.totp;

import org.jspecify.annotations.Nullable;
import java.time.Instant;

/**
 * Verifies TOTP codes against a verification window.
 *
 * <p>{@link #verify(String, String, Instant)} returns a {@link TotpVerification} carrying both the
 * outcome and the matched time step.
 */
public final class TotpVerifier {
    /**
     * Returned by {@link TotpVerification#timeStep()} when the code is invalid or matches no step
     * in the verification window. Time steps are never negative, so this value cannot collide with
     * a real step.
     */
    public static final long NO_MATCH = -1L;

    /** Default number of time steps accepted before and after the current step. */
    public static final int DEFAULT_VERIFICATION_WINDOW = 1;

    private final TotpGenerator generator;
    private final int window;

    /**
     * Creates a verifier with the default verification window.
     *
     * @param generator the generator
     */
    public TotpVerifier(TotpGenerator generator) {
        this(generator, DEFAULT_VERIFICATION_WINDOW);
    }

    /**
     * Creates a verifier.
     *
     * @param generator the generator
     * @param window the number of time steps accepted before and after the current step
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
     * Verifies a code against the current time.
     *
     * @param code the code
     * @param secret the secret
     * @return the verification outcome and the matched time step
     */
    public TotpVerification verify(@Nullable String code, @Nullable String secret) {
        return verify(code, secret, Instant.now());
    }

    /**
     * Verifies a code against the given time.
     *
     * <p>A code matches when it equals the code generated for any step in the verification window
     * around the step containing {@code time} (steps {@code -window} to {@code +window}). Whitespace
     * in the code is ignored. When more than one step matches, the lowest one is reported.
     *
     * @param code the code
     * @param secret the secret
     * @param time the time
     * @return the verification outcome and the matched time step
     */
    public TotpVerification verify(@Nullable String code, @Nullable String secret, @Nullable Instant time) {
        if (code == null || secret == null || time == null) {
            return new TotpVerification(NO_MATCH);
        }
        String normalized = normalize(code);
        if (normalized.length() != generator.digits()) {
            return new TotpVerification(NO_MATCH);
        }
        if (!normalized.chars().allMatch(Character::isDigit)) {
            return new TotpVerification(NO_MATCH);
        }
        long counter = Math.floorDiv(time.getEpochSecond(), generator.periodSeconds());
        for (int i = -window; i <= window; i++) {
            long candidate = counter + i;
            if (candidate < 0) {
                continue;
            }
            String expected = generator.generate(secret, candidate);
            if (constantTimeEquals(expected, normalized)) {
                return new TotpVerification(candidate);
            }
        }
        return new TotpVerification(NO_MATCH);
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
