package io.github.allbegray.totp;

/**
 * The outcome of verifying a TOTP code: whether it was valid and, when it was, which time step it
 * matched.
 *
 * <p>The matched time step is the single source of truth — {@link #timeStep()} returns
 * {@link TotpVerifier#NO_MATCH} exactly when {@link #isValid()} is false — so the two can never
 * disagree. Instances are produced by {@link TotpVerifier#verify}.
 */
public final class TotpVerification {
    private final long timeStep;

    TotpVerification(long timeStep) {
        this.timeStep = timeStep;
    }

    /**
     * Whether the code matched a step in the verification window.
     *
     * @return true when the code is valid
     */
    public boolean isValid() {
        return timeStep != TotpVerifier.NO_MATCH;
    }

    /**
     * The time step the code matched.
     *
     * @return the matched time step, or {@link TotpVerifier#NO_MATCH} when the code is invalid or
     * matches no step
     */
    public long timeStep() {
        return timeStep;
    }
}
