package io.github.allbegray.totp;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class Base32Test {
    @Test
    void decodeAcceptsLowercaseInputRegardlessOfDefaultLocale() {
        byte[] data = "foobar".getBytes(StandardCharsets.US_ASCII);
        String lowercase = Base32.encode(data).toLowerCase(Locale.ROOT);

        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertArrayEquals(data, Base32.decode(lowercase));
        } finally {
            Locale.setDefault(previous);
        }
    }
}
