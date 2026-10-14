# Security Notes

- Secrets are generated with `SecureRandom`.
- Verification uses constant-time string comparison.
- Replay protection for TOTP is application state and is intentionally not hidden inside this stateless library. Store and check the last accepted time step in your persistence layer.
- Keep secrets encrypted at rest and never log generated codes or URIs containing secrets.