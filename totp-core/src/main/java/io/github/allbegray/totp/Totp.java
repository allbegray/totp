package io.github.allbegray.totp;

import org.jspecify.annotations.Nullable;

import java.time.Instant;

/** Totp. */
public final class Totp {
    private final TotpSecretGenerator secretGenerator;
    private final TotpGenerator generator;
    private final TotpVerifier verifier;

    private Totp(TotpBuilder builder) {
        this.secretGenerator = new TotpSecretGenerator(builder.secretBytes);
        this.generator = new TotpGenerator(builder.digits, builder.periodSeconds, builder.algorithm);
        this.verifier = new TotpVerifier(this.generator, builder.verificationWindow);
    }

    private Totp(TotpSecretGenerator secretGenerator, TotpGenerator generator, TotpVerifier verifier) {
        this.secretGenerator = secretGenerator;
        this.generator = generator;
        this.verifier = verifier;
    }
    /**
     * defaults.*
     * @return the result
     */

    public static Totp defaults() {
        return builder().build();
    }
    /**
     * of.*
     *
     * @param secretGenerator the secretGenerator
     * @param generator the generator
     * @param verifier the verifier
     * @return the result
     */

    public static Totp of(TotpSecretGenerator secretGenerator, TotpGenerator generator, TotpVerifier verifier) {
        return new Totp(secretGenerator, generator, verifier);
    }
    /**
     * builder.*
     * @return the result
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
     * verifyCode.*
     *
     * @param code the code
     * @param secret the secret
     * @return the result
     */

    public boolean verifyCode(String code, String secret) {
        return verifier.verify(code, secret);
    }
    /**
     * verifyCode.*
     *
     * @param code the code
     * @param secret the secret
     * @param time the time
     * @return the result
     */

    public boolean verifyCode(String code, String secret, Instant time) {
        return verifier.verify(code, secret, time);
    }
    /**
     * otpAuthUri.*
     *
     * @param issuer the issuer
     * @param account the account
     * @param secret the secret
     * @return the result
     */

    public String otpAuthUri(@Nullable String issuer, @Nullable String account, String secret) {
        return TotpGenerator.otpAuthUri(issuer, account, secret, generator.algorithm(), generator.digits(), generator.periodSeconds());
    }

    /** Builder. */
    public static final class TotpBuilder {
        /** Creates a builder with default settings. */
        public TotpBuilder() {
        }

        private int digits = 6;
        private long periodSeconds = 30L;
        private TotpAlgorithm algorithm = TotpAlgorithm.SHA1;
        private int secretBytes = 20;
        private int verificationWindow = 1;
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