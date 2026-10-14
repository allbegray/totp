package io.github.allbegray.totp;

import org.jspecify.annotations.Nullable;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/** Base32. */
public final class Base32 {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    private Base32() {
    }
    /**
     * encode.*
     *
     * @param data the data
     * @return the result
     */

    public static String encode(@Nullable byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        int buffer = data[0] & 0xff;
        int next = 1;
        int bitsLeft = 8;
        while (bitsLeft > 0 || next < data.length) {
            if (bitsLeft < 5) {
                if (next < data.length) {
                    buffer <<= 8;
                    buffer |= data[next] & 0xff;
                    next++;
                    bitsLeft += 8;
                } else {
                    int pad = 5 - bitsLeft;
                    buffer <<= pad;
                    bitsLeft += pad;
                }
            }
            int index = (buffer >> (bitsLeft - 5)) & 0x1f;
            bitsLeft -= 5;
            out.append(ALPHABET.charAt(index));
        }
        return out.toString();
    }
    /**
     * decode.*
     *
     * @param input the input
     * @return the result
     */

    public static byte[] decode(@Nullable String input) {
        if (input == null || input.isEmpty()) {
            return new byte[0];
        }
        String cleaned = input.replaceAll("[^A-Za-z0-9=]", "").toUpperCase();
        // Remove any existing padding then re-pad to a multiple of 8.
        String data = cleaned.replace("=", "");
        if (data.isEmpty()) {
            return new byte[0];
        }
        int padLen = (8 - (data.length() % 8)) % 8;
        StringBuilder padded = new StringBuilder(data);
        for (int i = 0; i < padLen; i++) {
            padded.append('=');
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int buffer = 0;
        int bitsLeft = 0;
        for (int i = 0; i < padded.length(); i++) {
            char c = padded.charAt(i);
            if (c == '=') {
                break;
            }
            int val = ALPHABET.indexOf(c);
            if (val < 0) {
                throw new IllegalArgumentException("Invalid Base32 character: " + c);
            }
            buffer = (buffer << 5) | val;
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                out.write((buffer >> (bitsLeft - 8)) & 0xff);
                bitsLeft -= 8;
            }
        }
        return out.toByteArray();
    }

}