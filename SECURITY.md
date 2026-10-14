# Security Notes

- Secrets are generated with `SecureRandom`.
- Verification uses constant-time string comparison.
- Replay protection for TOTP is application state and is intentionally not hidden inside this stateless library. Use `TotpVerifier.verifyAndGetTimeStep(...)` to get the matched time step, then store and compare it in your persistence layer.
- Keep secrets encrypted at rest and never log generated codes or URIs containing secrets.