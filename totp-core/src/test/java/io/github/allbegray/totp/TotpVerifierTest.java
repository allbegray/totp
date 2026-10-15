package io.github.allbegray.totp;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TotpVerifierTest {
    private static final String SECRET =
            Base32.encode("12345678901234567890".getBytes(StandardCharsets.US_ASCII));

    @Test
    void validCodeIsValidAndCarriesStep() {
        TotpGenerator generator = new TotpGenerator();
        Instant time = Instant.ofEpochSecond(59);
        String code = generator.generate(SECRET, time);

        TotpVerification verification = new TotpVerifier(generator).verify(code, SECRET, time);

        assertTrue(verification.isValid());
        assertEquals(1L, verification.timeStep());
    }

    @Test
    void returnsStepThatMatchedNotTheCurrentStep() {
        TotpGenerator generator = new TotpGenerator();
        Instant time = Instant.ofEpochSecond(90); // step 3
        String previousStepCode = generator.generate(SECRET, 2L); // step 2

        TotpVerification verification = new TotpVerifier(generator).verify(previousStepCode, SECRET, time);

        assertTrue(verification.isValid());
        assertEquals(2L, verification.timeStep());
    }

    @Test
    void acceptsStepsWithinWindowInBothDirections() {
        TotpGenerator generator = new TotpGenerator();
        TotpVerifier verifier = new TotpVerifier(generator);
        Instant time = Instant.ofEpochSecond(90); // step 3

        assertEquals(2L, verifier.verify(generator.generate(SECRET, 2L), SECRET, time).timeStep());
        assertEquals(4L, verifier.verify(generator.generate(SECRET, 4L), SECRET, time).timeStep());
    }

    @Test
    void rejectsStepsOutsideWindow() {
        TotpGenerator generator = new TotpGenerator();
        TotpVerifier verifier = new TotpVerifier(generator);
        Instant time = Instant.ofEpochSecond(90); // step 3

        TotpVerification before = verifier.verify(generator.generate(SECRET, 1L), SECRET, time);
        TotpVerification after = verifier.verify(generator.generate(SECRET, 5L), SECRET, time);

        assertFalse(before.isValid());
        assertFalse(after.isValid());
        assertEquals(TotpVerifier.NO_MATCH, before.timeStep());
        assertEquals(TotpVerifier.NO_MATCH, after.timeStep());
    }

    @Test
    void zeroWindowRejectsAdjacentSteps() {
        TotpGenerator generator = new TotpGenerator();
        TotpVerifier verifier = new TotpVerifier(generator, 0);
        Instant time = Instant.ofEpochSecond(90); // step 3

        assertEquals(3L, verifier.verify(generator.generate(SECRET, 3L), SECRET, time).timeStep());
        assertFalse(verifier.verify(generator.generate(SECRET, 2L), SECRET, time).isValid());
    }

    @Test
    void invalidInputsAreInvalidWithNoMatch() {
        TotpGenerator generator = new TotpGenerator();
        TotpVerifier verifier = new TotpVerifier(generator);
        Instant time = Instant.ofEpochSecond(59);
        String code = generator.generate(SECRET, time);

        assertInvalid(verifier, null, SECRET, time);
        assertInvalid(verifier, code, null, time);
        assertInvalid(verifier, code, SECRET, null);
        assertInvalid(verifier, "", SECRET, time);
        assertInvalid(verifier, "12345", SECRET, time);
        assertInvalid(verifier, "12a456", SECRET, time);
    }

    @Test
    void ignoresWhitespaceInCode() {
        TotpGenerator generator = new TotpGenerator();
        String code = generator.generate(SECRET, Instant.ofEpochSecond(59));
        String spaced = " " + code.substring(0, 3) + " " + code.substring(3) + " ";

        TotpVerification verification = new TotpVerifier(generator).verify(spaced, SECRET, Instant.ofEpochSecond(59));

        assertTrue(verification.isValid());
        assertEquals(1L, verification.timeStep());
    }

    @Test
    void repeatedVerificationReportsSameStep() {
        TotpGenerator generator = new TotpGenerator();
        TotpVerifier verifier = new TotpVerifier(generator);
        Instant time = Instant.ofEpochSecond(59);
        String code = generator.generate(SECRET, time);

        TotpVerification first = verifier.verify(code, SECRET, time);
        TotpVerification second = verifier.verify(code, SECRET, time);

        assertTrue(first.isValid());
        assertEquals(first.timeStep(), second.timeStep());

        // The canonical replay rule: accept a code only when its matched step is greater than the
        // last accepted one, so a repeated verification must be rejected.
        assertFalse(second.timeStep() > first.timeStep());
    }

    @Test
    void facadeReturnsVerification() {
        Totp totp = Totp.defaults();
        Instant time = Instant.ofEpochSecond(59);
        String code = totp.generateCode(SECRET, time);

        TotpVerification verification = totp.verifyCode(code, SECRET, time);
        assertTrue(verification.isValid());
        assertEquals(1L, verification.timeStep());

        TotpVerification invalid = totp.verifyCode("00000", SECRET, time);
        assertFalse(invalid.isValid());
        assertEquals(TotpVerifier.NO_MATCH, invalid.timeStep());
    }

    private static void assertInvalid(TotpVerifier verifier, String code, String secret, Instant time) {
        TotpVerification verification = verifier.verify(code, secret, time);

        assertFalse(verification.isValid());
        assertEquals(TotpVerifier.NO_MATCH, verification.timeStep());
    }
}
