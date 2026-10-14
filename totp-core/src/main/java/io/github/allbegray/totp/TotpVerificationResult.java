package io.github.allbegray.totp;

/**
 * Result of a TOTP verification attempt.
 *
 * @param valid     whether the supplied code matched a code in the accepted window
 * @param timeStep  the matched time step, or {@code -1} when verification failed
 */
public record TotpVerificationResult(boolean valid, long timeStep) {

    /**
     * Creates an unsuccessful result.
     *
     * @return an unsuccessful result
     */
    public static TotpVerificationResult invalid() {
        return new TotpVerificationResult(false, -1L);
    }
}