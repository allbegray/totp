# Migration from java-totp and GoogleAuth

## Concepts

- `TotpSecretGenerator.generate()` replaces secret generation helpers.
- `TotpGenerator.generate(secret)` / `generate(secret, time)` replaces code generation.
- `TotpVerifier.verify(code, secret)` replaces validation helpers and returns the matched time step (`TotpVerifier.NO_MATCH` when invalid).
- `Totp.otpAuthUri(...)` (or `TotpUri.build(...)`) replaces OTP URI / QR payload construction.

## Notes

- QR rendering is available through `TotpQrCodeGenerator`. ZXing (`com.google.zxing:core` and `com.google.zxing:javase`) is its implementation detail: no ZXing type appears in the interface, there are no checked exceptions to handle, and Maven/Gradle resolve it transitively. Otherwise, render the returned `otpauth://` URI with any QR library.
- This library targets Java 8 bytecode and runs on Java 8+ (compatible with Spring Boot 2.x/3.x/4.x).

## Upgrading from totp 1.0.5

1.0.6 is a breaking release: the library is in early adoption, so interfaces were corrected rather than preserved. Changes:

- `Totp.verifyCode(...)` and `TotpVerifier.verify(...)` return a `TotpVerification` carrying both the boolean outcome (`isValid()`) and the matched time step (`timeStep()`, `TotpVerifier.NO_MATCH` when invalid) instead of `boolean`.
- `Totp.of(...)` is removed; `Totp.builder()` is the only assembly path.
- The per-call `verify(..., window)` overload is removed; configure the window via `Totp.builder().verificationWindow(...)` or `new TotpVerifier(generator, window)`.
- `TotpGenerator.otpAuthUri(...)` is removed; use `Totp.otpAuthUri(...)` or `TotpUri.build(...)`.
- QR rendering declares no checked exceptions; ZXing failures surface as `IllegalStateException`.
- `Base32` is no longer part of the public interface.
- `digits` supports 1 to 9 (`10` silently produced incorrect codes and is now rejected).