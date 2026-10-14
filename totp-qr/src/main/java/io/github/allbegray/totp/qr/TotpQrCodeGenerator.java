package io.github.allbegray.totp.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.jspecify.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class TotpQrCodeGenerator {
    private final int defaultSize;

    public TotpQrCodeGenerator() {
        this(256);
    }

    public TotpQrCodeGenerator(int defaultSize) {
        if (defaultSize <= 0) {
            throw new IllegalArgumentException("defaultSize must be positive");
        }
        this.defaultSize = defaultSize;
    }

    public BufferedImage toImage(String content) throws WriterException {
        return toImage(content, defaultSize);
    }

    public BufferedImage toImage(String content, int size) throws WriterException {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size must be positive");
        }
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size);
        return MatrixToImageWriter.toBufferedImage(matrix);
    }

    public byte[] toPng(String content) throws WriterException, IOException {
        return toPng(content, defaultSize);
    }

    public byte[] toPng(String content, int size) throws WriterException, IOException {
        BufferedImage image = toImage(content, size);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            javax.imageio.ImageIO.write(image, "PNG", out);
            return out.toByteArray();
        }
    }
}