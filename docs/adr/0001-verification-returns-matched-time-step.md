# Verification returns one result carrying the outcome and the matched time step

Replay protection is documented as application state keyed by the last accepted time step, and neither a boolean result (which hides which step matched) nor a bare step (which makes the common yes/no call site clumsy) suffices on its own. `verify(...)` therefore returns a small value object, `TotpVerification`: its `timeStep()` is the single source of truth — the matched time step, or `TotpVerifier.NO_MATCH` (`-1`, impossible for a real step) when nothing matched — and `isValid()` is derived from it, so the outcome and the step can never disagree; the constructor is package-private, so only verification produces instances. A 1.0.6 attempt with a two-field result record was reverted, and this version revisited the idea as a Java 8 final class with no containers.

## Considered options

- A single `long` return with callers deriving booleans: rejected — every yes/no call site pays for a feature it does not use, and `!= NO_MATCH` reads poorly.
- Boolean result with a `matchedTimeStep(...)` companion method: rejected — one verification question should be one call; two methods doing the same work invite drift.
- A record-based two-field result (Java 16+): rejected — records are unavailable at the Java 8 target, and two independently stored fields can disagree; a single derived source cannot.
- `OptionalLong` or boxed `@Nullable Long`: rejected — containers add ceremony at every call site; a documented impossible-value sentinel composes with the monotonic replay rule.
- Matching the ecosystem default (boolean-only verification, as in GoogleAuth, java-totp, java-otp, and otp-java): rejected — callers would have to re-derive the counter and re-scan codes, and the documented replay rule silently breaks.

## Consequences

- `verify` and `verifyCode` no longer return `boolean`; callers use `.isValid()`.
- One call serves both verification and replay protection: `verification.isValid() && verification.timeStep() > lastAccepted`.
- The per-call `customWindow` parameter is removed; the verification window is constructor configuration.
