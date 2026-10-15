package io.github.allbegray.totp.qr;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TotpQrCodeGeneratorTests {
    private static final String CONTENT = "otpauth://totp/test:account?secret=ABC";

    @Test
    void rendersPngRoundTrip() throws Exception {
        byte[] png = new TotpQrCodeGenerator().toPng(CONTENT, 64);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(png));

        assertNotNull(image);
        assertEquals(64, image.getWidth());
        assertEquals(64, image.getHeight());
    }

    @Test
    void usesDefaultSizeWhenSizeIsOmitted() throws Exception {
        byte[] png = new TotpQrCodeGenerator().toPng(CONTENT);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(png));

        assertNotNull(image);
        assertEquals(TotpQrCodeGenerator.DEFAULT_SIZE, image.getWidth());
    }

    @Test
    void rendersImage() {
        BufferedImage image = new TotpQrCodeGenerator().toImage(CONTENT, 64);

        assertEquals(64, image.getWidth());
        assertEquals(64, image.getHeight());
    }

    @Test
    void rejectsBlankContentAndBadSize() {
        TotpQrCodeGenerator generator = new TotpQrCodeGenerator();

        assertThrows(IllegalArgumentException.class, () -> generator.toPng("  "));
        assertThrows(IllegalArgumentException.class, () -> generator.toPng(CONTENT, 0));
        assertThrows(IllegalArgumentException.class, () -> generator.toImage(null));
        assertThrows(IllegalArgumentException.class, () -> new TotpQrCodeGenerator(0));
    }
}
