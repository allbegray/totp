package io.github.allbegray.totp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TotpUriTest {
    private static final String SECRET = "GEZDGNBVGY3TQOJQ";

    @Test
    void buildsFullUri() {
        String uri = TotpUri.build("Example", "alice@example.com", SECRET, TotpAlgorithm.SHA256, 8, 60);

        assertEquals("otpauth://totp/Example:alice%40example.com?secret=GEZDGNBVGY3TQOJQ"
                + "&issuer=Example&algorithm=SHA256&digits=8&period=60", uri);
    }

    @Test
    void omitsIssuerWhenAbsent() {
        String uri = TotpUri.build(null, "alice@example.com", SECRET, TotpAlgorithm.SHA1, 6, 30);

        assertEquals("otpauth://totp/alice%40example.com?secret=GEZDGNBVGY3TQOJQ"
                + "&algorithm=SHA1&digits=6&period=30", uri);
    }

    @Test
    void encodesSpacesAndPlus() {
        String uri = TotpUri.build("My Issuer", "a+b", SECRET, null, 6, 30);

        assertTrue(uri.contains("My%20Issuer:a%2Bb"));
        assertFalse(uri.contains("algorithm="));
    }

    @Test
    void percentEncodesNonAsciiUtf8() {
        String uri = TotpUri.build("내서비스", "홍길동", SECRET, TotpAlgorithm.SHA1, 6, 30);

        assertEquals("otpauth://totp/%EB%82%B4%EC%84%9C%EB%B9%84%EC%8A%A4:%ED%99%8D%EA%B8%B8%EB%8F%99"
                + "?secret=GEZDGNBVGY3TQOJQ&issuer=%EB%82%B4%EC%84%9C%EB%B9%84%EC%8A%A4"
                + "&algorithm=SHA1&digits=6&period=30", uri);
    }

    @Test
    void facadeBuildsUriFromItsConfiguration() {
        Totp totp = Totp.builder().algorithm(TotpAlgorithm.SHA512).digits(8).periodSeconds(60).build();

        String uri = totp.otpAuthUri("Example", "account", SECRET);

        assertEquals("otpauth://totp/Example:account?secret=GEZDGNBVGY3TQOJQ"
                + "&issuer=Example&algorithm=SHA512&digits=8&period=60", uri);
    }

    @Test
    void labelIsIssuerWhenAccountIsAbsent() {
        String uri = TotpUri.build("Example", null, SECRET, TotpAlgorithm.SHA1, 6, 30);

        assertEquals("otpauth://totp/Example?secret=GEZDGNBVGY3TQOJQ"
                + "&issuer=Example&algorithm=SHA1&digits=6&period=30", uri);
    }

    @Test
    void rejectsBlankSecret() {
        assertThrows(IllegalArgumentException.class,
                () -> TotpUri.build("Example", "account", "  ", TotpAlgorithm.SHA1, 6, 30));
    }
}
