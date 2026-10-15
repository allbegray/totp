package io.github.allbegray.totp;

import org.jspecify.annotations.Nullable;

import java.time.Instant;

/**
 * Fluent entry point that wires secret generation, code generation, and verification from one
 * configuration. Obtain instances via {@link #defaults()} or {@link #builder()}.
 *
 * <p>Instances are immutable and thread-safe, so one instance can be shared across threads (for
 * example as an application-wide singleton). Per-account secrets are passed to the methods rather
 * than stored in the instance.
 */
public final class Totp {
    private final TotpSecretGenerator secretGenerator;
    private final TotpGenerator generator;
    private final TotpVerifier verifier;

    private Totp(TotpBuilder builder) {
        this.secretGenerator = new TotpSecretGenerator(builder.secretBytes);
        this.generator = new TotpGenerator(builder.digits, builder.periodSeconds, builder.algorithm);
        this.verifier = new TotpVerifier(this.generator, builder.verificationWindow);
    }

    /**
     * Creates a Totp with default settings.
     *
     * @return a Totp with default settings
     */

    public static Totp defaults() {
        return builder().build();
    }
    /**
     * Creates a builder for custom settings.
     *
     * @return a new builder
     */

    public static TotpBuilder builder() {
        return new TotpBuilder();
    }
    /**
     * digits.*
     * @return the result
     */

    public int digits() {
        return generator.digits();
    }
    /**
     * periodSeconds.*
     * @return the result
     */

    public long periodSeconds() {
        return generator.periodSeconds();
    }
    /**
     * algorithm.*
     * @return the result
     */

    public TotpAlgorithm algorithm() {
        return generator.algorithm();
    }
    /**
     * generateSecret.*
     * @return the result
     */

    public String generateSecret() {
        return secretGenerator.generate();
    }
    /**
     * generateCode.*
     *
     * @param secret the secret
     * @return the result
     */

    public String generateCode(String secret) {
        return generator.generate(secret);
    }
    /**
     * generateCode.*
     *
     * @param secret the secret
     * @param time the time
     * @return the result
     */

    public String generateCode(String secret, Instant time) {
        return generator.generate(secret, time);
    }
    /**
     * Verifies a code against the current time.
     *
     * @param code the code
     * @param secret the secret
     * @return the verification outcome and the matched time step
     */

    public TotpVerification verifyCode(@Nullable String code, @Nullable String secret) {
        return verifier.verify(code, secret);
    }
    /**
     * Verifies a code against the given time.
     *
     * @param code the code
     * @param secret the secret
     * @param time the time
     * @return the verification outcome and the matched time step
     */

    public TotpVerification verifyCode(@Nullable String code, @Nullable String secret, @Nullable Instant time) {
        return verifier.verify(code, secret, time);
    }
    /**
     * Builds the otpauth URI for this configuration, encoding the algorithm, digits, and period.
     *
     * @param issuer the issuer
     * @param account the account
     * @param secret the secret
     * @return the otpauth URI
     */

    public String otpAuthUri(@Nullable String issuer, @Nullable String account, String secret) {
        return TotpUri.build(issuer, account, secret, generator.algorithm(), generator.digits(), generator.periodSeconds());
    }

    /**
     * Builder for {@link Totp}. Not thread-safe: create and use one builder per configuration.
     */
    public static final class TotpBuilder {
        private int digits = TotpGenerator.DEFAULT_DIGITS;
        private long periodSeconds = TotpGenerator.DEFAULT_PERIOD_SECONDS;
        private TotpAlgorithm algorithm = TotpAlgorithm.SHA1;
        private int secretBytes = TotpSecretGenerator.DEFAULT_SECRET_BYTES;
        private int verificationWindow = TotpVerifier.DEFAULT_VERIFICATION_WINDOW;

        /** Creates a builder with default settings. */
        private TotpBuilder() {
        }
        /**
         * digits.*
         *
         * @param digits the digits
         * @return the result
         */

        public TotpBuilder digits(int digits) {
            this.digits = digits;
            return this;
        }
        /**
         * periodSeconds.*
         *
         * @param periodSeconds the periodSeconds
         * @return the result
         */

        public TotpBuilder periodSeconds(long periodSeconds) {
            this.periodSeconds = periodSeconds;
            return this;
        }
        /**
         * algorithm.*
         *
         * @param algorithm the algorithm
         * @return the result
         */

        public TotpBuilder algorithm(TotpAlgorithm algorithm) {
            this.algorithm = algorithm;
            return this;
        }
        /**
         * secretBytes.*
         *
         * @param secretBytes the secretBytes
         * @return the result
         */

        public TotpBuilder secretBytes(int secretBytes) {
            this.secretBytes = secretBytes;
            return this;
        }
        /**
         * verificationWindow.*
         *
         * @param verificationWindow the verificationWindow
         * @return the result
         */

        public TotpBuilder verificationWindow(int verificationWindow) {
            this.verificationWindow = verificationWindow;
            return this;
        }
        /**
         * build.*
         * @return the result
         */

        public Totp build() {
            return new Totp(this);
        }
    }
}