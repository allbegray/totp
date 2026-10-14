package io.github.allbegray.totp;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TotpGeneratorTest {
    @Test
    void rfc6238Sha1Vector59() {
        String secret = Base32.encode("12345678901234567890".getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        TotpGenerator generator = new TotpGenerator(8, 30, TotpAlgorithm.SHA1);
        assertEquals("94287082", generator.generate(secret, Instant.ofEpochSecond(59)));
    }

    @Test
    void rfc6238Sha1Vector1111111109() {
        String secret = Base32.encode("12345678901234567890".getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        TotpGenerator generator = new TotpGenerator(8, 30, TotpAlgorithm.SHA1);
        assertEquals("07081804", generator.generate(secret, Instant.ofEpochSecond(1111111109L)));
    }

    @Test
    void verifierAcceptsValidCode() {
        TotpGenerator generator = new TotpGenerator();
        String secret = new TotpSecretGenerator().generate();
        String code = generator.generate(secret);
        TotpVerifier verifier = new TotpVerifier(generator, 1);
        org.junit.jupiter.api.Assertions.assertTrue(verifier.verify(code, secret));
    }
}