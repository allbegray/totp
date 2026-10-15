package io.github.allbegray.totp.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Renders QR code images for otpauth URIs.
 *
 * <p>ZXing is an implementation detail: no ZXing type appears in this interface, and encoding
 * failures surface as {@link IllegalStateException}.
 */
public final class TotpQrCodeGenerator {
    /** Default image size in pixels. */
    public static final int DEFAULT_SIZE = 256;

    private final int defaultSize;

    /** Creates a generator with the default image size. */
    public TotpQrCodeGenerator() {
        this(DEFAULT_SIZE);
    }

    /**
     * Creates a generator.
     *
     * @param defaultSize the default image size in pixels
     */
    public TotpQrCodeGenerator(int defaultSize) {
        if (defaultSize <= 0) {
            throw new IllegalArgumentException("defaultSize must be positive");
        }
        this.defaultSize = defaultSize;
    }

    /**
     * Renders content as a QR code image with the default size.
     *
     * @param content the content
     * @return the image
     */
    public BufferedImage toImage(String content) {
        return toImage(content, defaultSize);
    }

    /**
     * Renders content as a QR code image.
     *
     * @param content the content
     * @param size the image size in pixels
     * @return the image
     */
    public BufferedImage toImage(String content, int size) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("content must not be blank");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size must be positive");
        }
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size);
            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (WriterException e) {
            throw new IllegalStateException("Unable to render QR code", e);
        }
    }

    /**
     * Renders content as a PNG image with the default size.
     *
     * @param content the content
     * @return the PNG bytes
     */
    public byte[] toPng(String content) {
        return toPng(content, defaultSize);
    }

    /**
     * Renders content as a PNG image.
     *
     * @param content the content
     * @param size the image size in pixels
     * @return the PNG bytes
     */
    public byte[] toPng(String content, int size) {
        BufferedImage image = toImage(content, size);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            if (!ImageIO.write(image, "PNG", out)) {
                throw new IllegalStateException("No PNG image writer available");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to encode PNG", e);
        }
        return out.toByteArray();
    }
}
