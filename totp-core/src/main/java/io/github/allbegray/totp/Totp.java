package io.github.allbegray.totp;

import org.jspecify.annotations.Nullable;

import java.time.Instant;

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

    public static Totp defaults() {
        return builder().build();
    }

    public static Totp of(TotpSecretGenerator secretGenerator, TotpGenerator generator, TotpVerifier verifier) {
        return new Totp(secretGenerator, generator, verifier);
    }

    public static TotpBuilder builder() {
        return new TotpBuilder();
    }

    public int digits() {
        return generator.digits();
    }

    public long periodSeconds() {
        return generator.periodSeconds();
    }

    public TotpAlgorithm algorithm() {
        return generator.algorithm();
    }

    public String generateSecret() {
        return secretGenerator.generate();
    }

    public String generateCode(String secret) {
        return generator.generate(secret);
    }

    public String generateCode(String secret, Instant time) {
        return generator.generate(secret, time);
    }

    public boolean verifyCode(String code, String secret) {
        return verifier.verify(code, secret);
    }

    public boolean verifyCode(String code, String secret, Instant time) {
        return verifier.verify(code, secret, time);
    }

    public String otpAuthUri(@Nullable String issuer, @Nullable String account, String secret) {
        return TotpGenerator.otpAuthUri(issuer, account, secret, generator.algorithm(), generator.digits(), generator.periodSeconds());
    }

    public static final class TotpBuilder {
        private int digits = 6;
        private long periodSeconds = 30L;
        private TotpAlgorithm algorithm = TotpAlgorithm.SHA1;
        private int secretBytes = 20;
        private int verificationWindow = 1;

        public TotpBuilder digits(int digits) {
            this.digits = digits;
            return this;
        }

        public TotpBuilder periodSeconds(long periodSeconds) {
            this.periodSeconds = periodSeconds;
            return this;
        }

        public TotpBuilder algorithm(TotpAlgorithm algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public TotpBuilder secretBytes(int secretBytes) {
            this.secretBytes = secretBytes;
            return this;
        }

        public TotpBuilder verificationWindow(int verificationWindow) {
            this.verificationWindow = verificationWindow;
            return this;
        }

        public Totp build() {
            return new Totp(this);
        }
    }
}