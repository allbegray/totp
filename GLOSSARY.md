# totp

Domain language for generating and verifying TOTP (RFC 6238) codes: the intervals codes belong to, the tolerance for clock drift, and the rule that makes a code usable only once.

## Language

**Time step**:
The integer index of the interval a code belongs to: the Unix time divided by the period length. Codes, verification windows, and replay protection are all expressed in time steps.
_Avoid_: bucket, interval index, counter (the RFC 4226 name for the same quantity in the HOTP context)

**Verification window**:
The number of time steps before and after the current step in which a submitted code is accepted, tolerating clock drift between the client and the verifier. A window of zero accepts only the current step.
_Avoid_: tolerance, discrepancy, drift allowance

**Matched time step**:
The time step whose generated code equals the submitted code. Verification returns it when one exists and signals absence otherwise.
_Avoid_: matched counter, matched offset

**Replay protection**:
The application-side rule that a code may be accepted at most once: remember the last accepted time step per account and accept a code only when its matched time step is greater. The library is stateless and deliberately does not hold this state.
_Avoid_: anti-replay, reuse prevention

**otpauth URI**:
The `otpauth://totp/...` URI that authenticator apps understand; it encodes the issuer, account, secret, and code parameters (algorithm, digits, period).
_Avoid_: OTP URL, auth link
