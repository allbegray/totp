package io.github.allbegray.totp;

import org.jspecify.annotations.Nullable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/** Builds otpauth URIs as understood by authenticator apps. */
public final class TotpUri {
    private TotpUri() {
    }
    /**
     * Builds an otpauth URI from the given parameters.
     *
     * <p>The issuer and account form the label ({@code issuer:account}) and are both optional.
     * When the account is absent, the label is the issuer alone.
     * The algorithm parameter is omitted when {@code algorithm} is null.
     *
     * @param issuer the issuer
     * @param account the account
     * @param secret the secret
     * @param algorithm the algorithm
     * @param digits the digits
     * @param periodSeconds the period in seconds
     * @return the otpauth URI
     */

    public static String build(@Nullable String issuer, @Nullable String account, String secret, @Nullable TotpAlgorithm algorithm, int digits, long periodSeconds) {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException("secret must not be blank");
        }
        boolean hasIssuer = issuer != null && !issuer.trim().isEmpty();
        boolean hasAccount = account != null && !account.trim().isEmpty();

        StringBuilder sb = new StringBuilder("otpauth://totp/");
        if (hasIssuer) {
            sb.append(urlEncode(issuer));
            if (hasAccount) {
                sb.append(":");
            }
        }
        if (hasAccount) {
            sb.append(urlEncode(account));
        }
        sb.append("?secret=").append(urlEncode(secret));
        if (hasIssuer) {
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
        try {
            return URLEncoder.encode(value, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 must be supported", e);
        }
    }
}