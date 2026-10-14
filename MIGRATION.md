# Migration from java-totp and GoogleAuth

## Concepts

- `TotpSecretGenerator.generate()` replaces secret generation helpers.
- `TotpGenerator.generate(secret)` / `generate(secret, time)` replaces code generation.
- `TotpVerifier.verify(code, secret)` replaces validation helpers.
- `TotpGenerator.otpAuthUri(...)` replaces OTP URI / QR payload construction.

## Notes

- QR rendering is available through `TotpQrCodeGenerator` when ZXing (`com.google.zxing:core` and `com.google.zxing:javase`) is on the classpath. Otherwise, render the returned `otpauth://` URI with any QR library.
- This library targets Java 17 and Spring Boot 3.x/4.x.