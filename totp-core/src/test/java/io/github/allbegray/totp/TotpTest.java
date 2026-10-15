package io.github.allbegray.totp;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TotpTest {
    private static final String SECRET =
            Base32.encode("12345678901234567890".getBytes(StandardCharsets.US_ASCII));

    @Test
    void defaultsUseCanonicalValues() {
        Totp totp = Totp.defaults();

        assertEquals(6, totp.digits());
        assertEquals(30L, totp.periodSeconds());
        assertEquals(TotpAlgorithm.SHA1, totp.algorithm());
        assertEquals(32, totp.generateSecret().length()); // 20 bytes -> 32 Base32 characters
    }

    @Test
    void generateAndVerifyShareOneConfiguration() {
        Totp totp = Totp.builder()
                .digits(8)
                .periodSeconds(60)
                .algorithm(TotpAlgorithm.SHA256)
                .verificationWindow(2)
                .build();
        Instant time = Instant.ofEpochSecond(90); // step 1 for a 60 second period

        assertEquals(8, totp.digits());
        assertEquals(60L, totp.periodSeconds());
        assertEquals(TotpAlgorithm.SHA256, totp.algorithm());

        String code = totp.generateCode(SECRET, time);

        TotpVerification verification = totp.verifyCode(code, SECRET, time);
        assertTrue(verification.isValid());
        assertEquals(1L, verification.timeStep());
    }

    @Test
    void facadeRespectsConfiguredVerificationWindow() {
        Totp totp = Totp.builder().verificationWindow(2).build();
        Instant verifyAt = Instant.ofEpochSecond(90); // step 3

        String codeFromStep1 = totp.generateCode(SECRET, Instant.ofEpochSecond(31)); // step 1
        TotpVerification step1 = totp.verifyCode(codeFromStep1, SECRET, verifyAt);
        assertTrue(step1.isValid());
        assertEquals(1L, step1.timeStep());

        String codeFromStep0 = totp.generateCode(SECRET, Instant.ofEpochSecond(0)); // step 0
        TotpVerification step0 = totp.verifyCode(codeFromStep0, SECRET, verifyAt);
        assertFalse(step0.isValid());
        assertEquals(TotpVerifier.NO_MATCH, step0.timeStep());
    }

    @Test
    void secretBytesConfiguresSecretLength() {
        Totp totp = Totp.builder().secretBytes(32).build();

        assertEquals(52, totp.generateSecret().length()); // 32 bytes -> 52 Base32 characters
    }
}
