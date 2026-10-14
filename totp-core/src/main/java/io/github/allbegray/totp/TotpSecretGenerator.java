package io.github.allbegray.totp;

import java.security.SecureRandom;

/** TotpSecretGenerator. */
public final class TotpSecretGenerator {
    /** Default secret length in bytes. */
    public static final int DEFAULT_SECRET_BYTES = 20;

    private final int bytes;
    private final SecureRandom random;
    /**
     * TotpSecretGenerator.*
     */

    public TotpSecretGenerator() {
        this(DEFAULT_SECRET_BYTES);
    }
    /**
     * TotpSecretGenerator.*
     *
     * @param bytes the bytes
     */

    public TotpSecretGenerator(int bytes) {
        this(bytes, new SecureRandom());
    }
    /**
     * TotpSecretGenerator.*
     *
     * @param bytes the bytes
     * @param random the random
     */

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
    /**
     * generate.*
     * @return the result
     */

    public String generate() {
        byte[] buffer = new byte[bytes];
        random.nextBytes(buffer);
        return Base32.encode(buffer);
    }
}