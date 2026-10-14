package io.github.allbegray.totp;

import org.jspecify.annotations.Nullable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** TotpUri. */
public final class TotpUri {
    private TotpUri() {
    }
    /**
     * build.*
     *
     * @param issuer the issuer
     * @param account the account
     * @param secret the secret
     * @param algorithm the algorithm
     * @param digits the digits
     * @param periodSeconds the periodSeconds
     * @return the result
     */

    public static String build(@Nullable String issuer, @Nullable String account, String secret, @Nullable TotpAlgorithm algorithm, int digits, long periodSeconds) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("secret must not be blank");
        }
        StringBuilder sb = new StringBuilder("otpauth://totp/");
        if (issuer != null && !issuer.isBlank()) {
            sb.append(urlEncode(issuer)).append(":");
        }
        if (account != null && !account.isBlank()) {
            sb.append(urlEncode(account));
        }
        sb.append("?secret=").append(urlEncode(secret));
        if (issuer != null && !issuer.isBlank()) {
            sb.append("&issuer=").append(urlEncode(issuer));
        }
        if (algorithm != null) {
            sb.append("&algorithm=").append(algorithm.otpAuthName());
        }
        sb.append("&digits=").append(digits);
        sb.append("&period=").append(periodSeconds);
        return sb.toString();
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}