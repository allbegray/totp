package io.github.allbegray.totp;

public enum TotpAlgorithm {
    SHA1("HmacSHA1", "SHA1"),
    SHA256("HmacSHA256", "SHA256"),
    SHA512("HmacSHA512", "SHA512");

    private final String macAlgorithm;
    private final String otpAuthName;

    TotpAlgorithm(String macAlgorithm, String otpAuthName) {
        this.macAlgorithm = macAlgorithm;
        this.otpAuthName = otpAuthName;
    }

    public String macAlgorithm() {
        return macAlgorithm;
    }

    public String otpAuthName() {
        return otpAuthName;
    }
}