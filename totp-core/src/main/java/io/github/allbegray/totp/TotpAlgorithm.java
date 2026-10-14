package io.github.allbegray.totp;

/** TotpAlgorithm. */
public enum TotpAlgorithm {
    /** SHA1. */
    SHA1("HmacSHA1", "SHA1"),
    /** SHA256. */
    SHA256("HmacSHA256", "SHA256"),
    /** SHA512. */
    SHA512("HmacSHA512", "SHA512");

    private final String macAlgorithm;
    private final String otpAuthName;

    TotpAlgorithm(String macAlgorithm, String otpAuthName) {
        this.macAlgorithm = macAlgorithm;
        this.otpAuthName = otpAuthName;
    }
    /**
     * macAlgorithm.*
     * @return the result
     */

    public String macAlgorithm() {
        return macAlgorithm;
    }
    /**
     * otpAuthName.*
     * @return the result
     */

    public String otpAuthName() {
        return otpAuthName;
    }
}