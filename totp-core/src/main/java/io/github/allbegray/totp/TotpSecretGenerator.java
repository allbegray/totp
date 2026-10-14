package io.github.allbegray.totp;

import java.security.SecureRandom;

public final class TotpSecretGenerator {
    public static final int DEFAULT_SECRET_BYTES = 20;

    private final int bytes;
    private final SecureRandom random;

    public TotpSecretGenerator() {
        this(DEFAULT_SECRET_BYTES);
    }

    public TotpSecretGenerator(int bytes) {
        this(bytes, new SecureRandom());
    }

    public TotpSecretGenerator(int bytes, SecureRandom random) {
        if (bytes <= 0) {
            throw new IllegalArgumentException("bytes must be positive");
        }
        if (random == null) {
            throw new IllegalArgumentException("random must not be null");
        }
        this.bytes = bytes;
        this.random = random;
    }

    public String generate() {
        byte[] buffer = new byte[bytes];
        random.nextBytes(buffer);
        return Base32.encode(buffer);
    }
}