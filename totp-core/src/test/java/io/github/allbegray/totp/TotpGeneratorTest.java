package io.github.allbegray.totp;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void rfc6238Sha256Vector59() {
        String secret = Base32.encode("12345678901234567890123456789012".getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        TotpGenerator generator = new TotpGenerator(8, 30, TotpAlgorithm.SHA256);
        assertEquals("46119246", generator.generate(secret, Instant.ofEpochSecond(59)));
    }

    @Test
    void rfc6238Sha256Vector1111111109() {
        String secret = Base32.encode("12345678901234567890123456789012".getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        TotpGenerator generator = new TotpGenerator(8, 30, TotpAlgorithm.SHA256);
        assertEquals("68084774", generator.generate(secret, Instant.ofEpochSecond(1111111109L)));
    }

    @Test
    void rfc6238Sha512Vector59() {
        String secret = Base32.encode("1234567890123456789012345678901234567890123456789012345678901234".getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        TotpGenerator generator = new TotpGenerator(8, 30, TotpAlgorithm.SHA512);
        assertEquals("90693936", generator.generate(secret, Instant.ofEpochSecond(59)));
    }

    @Test
    void rfc6238Sha512Vector1111111109() {
        String secret = Base32.encode("1234567890123456789012345678901234567890123456789012345678901234".getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        TotpGenerator generator = new TotpGenerator(8, 30, TotpAlgorithm.SHA512);
        assertEquals("25091201", generator.generate(secret, Instant.ofEpochSecond(1111111109L)));
    }

    @Test
    void nineDigitsAreSupported() {
        String secret = Base32.encode("12345678901234567890".getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        TotpGenerator generator = new TotpGenerator(9, 30, TotpAlgorithm.SHA1);

        assertEquals(9, generator.generate(secret, Instant.ofEpochSecond(59)).length());
    }

    @Test
    void rejectsDigitsAboveNine() {
        assertThrows(IllegalArgumentException.class, () -> new TotpGenerator(10, 30, TotpAlgorithm.SHA1));
    }

    @Test
    void codesAreAsciiDigitsRegardlessOfDefaultLocale() {
        String secret = Base32.encode("12345678901234567890".getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        TotpGenerator generator = new TotpGenerator(8, 30, TotpAlgorithm.SHA1);

        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("ar-SA"));
            assertEquals("94287082", generator.generate(secret, Instant.ofEpochSecond(59)));
        } finally {
            Locale.setDefault(previous);
        }
    }
}
