package io.github.allbegray.totp.qr;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TotpQrCodeGeneratorTests {
    @Test
    void generatesPng() throws Exception {
        TotpQrCodeGenerator generator = new TotpQrCodeGenerator();
        byte[] png = generator.toPng("otpauth://totp/test:account?secret=ABC", 64);
        assertTrue(png.length > 0);
    }
}