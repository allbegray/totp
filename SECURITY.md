# Security Notes

- Secrets are generated with `SecureRandom`.
- Verification uses constant-time string comparison.
- Replay protection for TOTP is application state and is intentionally not hidden inside this stateless library. `verify(...)` and `verifyCode(...)` return a `TotpVerification` carrying both the boolean outcome and the matched time step (`TotpVerifier.NO_MATCH` when nothing matches); store the last accepted time step per account and accept a code only when its matched step is greater than the stored one.
- Keep secrets encrypted at rest and never log generated codes or URIs containing secrets.