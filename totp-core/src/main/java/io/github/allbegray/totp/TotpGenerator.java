package io.github.allbegray.totp;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Locale;

/** TotpGenerator. */
public final class TotpGenerator {
    /** Default number of digits. */
    public static final int DEFAULT_DIGITS = 6;
    /** Default time step in seconds. */
    public static final long DEFAULT_PERIOD_SECONDS = 30L;

    private final int digits;
    private final long periodSeconds;
    private final TotpAlgorithm algorithm;
    /**
     * TotpGenerator.*
     */

    public TotpGenerator() {
        this(DEFAULT_DIGITS, DEFAULT_PERIOD_SECONDS, TotpAlgorithm.SHA1);
    }
    /**
     * Creates a generator.
     *
     * @param digits the number of code digits (1 to 9)
     * @param periodSeconds the time step in seconds
     * @param algorithm the HMAC algorithm
     */

    public TotpGenerator(int digits, long periodSeconds, TotpAlgorithm algorithm) {
        // Up to 9 digits: the dynamic truncation basis is a 31-bit number.
        if (digits < 1 || digits > 9) {
            throw new IllegalArgumentException("digits must be between 1 and 9");
        }
        if (periodSeconds <= 0) {
            throw new IllegalArgumentException("periodSeconds must be positive");
        }
        if (algorithm == null) {
            throw new IllegalArgumentException("algorithm must not be null");
        }
        this.digits = digits;
        this.periodSeconds = periodSeconds;
        this.algorithm = algorithm;
    }
    /**
     * digits.*
     * @return the result
     */

    public int digits() {
        return digits;
    }
    /**
     * periodSeconds.*
     * @return the result
     */

    public long periodSeconds() {
        return periodSeconds;
    }
    /**
     * algorithm.*
     * @return the result
     */

    public TotpAlgorithm algorithm() {
        return algorithm;
    }
    /**
     * generate.*
     *
     * @param secret the secret
     * @return the result
     */

    public String generate(String secret) {
        return generate(secret, Instant.now());
    }
    /**
     * generate.*
     *
     * @param secret the secret
     * @param time the time
     * @return the result
     */

    public String generate(String secret, Instant time) {
        if (time == null) {
            throw new IllegalArgumentException("time must not be null");
        }
        long counter = Math.floorDiv(time.getEpochSecond(), periodSeconds);
        return generate(secret, counter);
    }
    /**
     * generate.*
     *
     * @param secret the secret
     * @param counter the counter
     * @return the result
     */

    public String generate(String secret, long counter) {
        byte[] key = Base32.decode(secret);
        return generate(key, counter);
    }
    /**
     * generate.*
     *
     * @param key the key
     * @param counter the counter
     * @return the result
     */

    public String generate(byte[] key, long counter) {
        if (key == null || key.length == 0) {
            throw new IllegalArgumentException("secret key must not be empty");
        }
        try {
            Mac mac = Mac.getInstance(algorithm.macAlgorithm());
            mac.init(new SecretKeySpec(key, algorithm.macAlgorithm()));
            byte[] hash = mac.doFinal(longToBytes(counter));
            int offset = hash[hash.length - 1] & 0x0f;
            int binary = ((hash[offset] & 0x7f) << 24)
                    | ((hash[offset + 1] & 0xff) << 16)
                    | ((hash[offset + 2] & 0xff) << 8)
                    | (hash[offset + 3] & 0xff);
            int modulus = (int) Math.pow(10, digits);
            int otp = binary % modulus;
            return String.format(Locale.ROOT, "%0" + digits + "d", otp);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Unable to generate TOTP", e);
        }
    }

    private static byte[] longToBytes(long value) {
        return ByteBuffer.allocate(8).putLong(value).array();
    }
}