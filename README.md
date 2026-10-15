# totp

A maintained TOTP implementation for the JVM.

It replaces unmaintained libraries such as `java-totp` and `GoogleAuth` and targets Java 8+.

## Modules

- `totp-core` - pure Java implementation of RFC 4226 (HOTP) and RFC 6238 (TOTP)
- `totp-qr` - optional QR code generation module using ZXing

## Features

- Generate cryptographically secure Base32 secrets
- Generate TOTP codes (SHA-1, SHA-256, SHA-512)
- Verify codes with a configurable time window; results carry the matched time step for replay protection
- Build `otpauth://` URIs for authenticator apps
- RFC 6238 test vectors verified
- JSpecify nullability annotations
- Fluent `Totp` facade for simple usage

## Installation

### Gradle

```gradle
dependencies {
    implementation("io.github.allbegray:totp-core:1.0.6")
}
```

Optional QR support:

```gradle
dependencies {
    implementation("io.github.allbegray:totp-core:1.0.6")
    implementation("io.github.allbegray:totp-qr:1.0.6")
}
```

### Maven

```xml
<dependency>
    <groupId>io.github.allbegray</groupId>
    <artifactId>totp-core</artifactId>
    <version>1.0.6</version>
</dependency>
```

Optional QR support:

```xml
<dependency>
    <groupId>io.github.allbegray</groupId>
    <artifactId>totp-qr</artifactId>
    <version>1.0.6</version>
</dependency>
```

## Usage

```java
Totp totp = Totp.defaults();

String secret = totp.generateSecret();
String code = totp.generateCode(secret);
TotpVerification verification = totp.verifyCode(code, secret); // isValid() and timeStep()
String uri = totp.otpAuthUri("Example", "alice@example.com", secret);
```

Custom settings:

```java
Totp totp = Totp.builder()
        .digits(8)
        .periodSeconds(60)
        .algorithm(TotpAlgorithm.SHA256)
        .verificationWindow(2)
        .build();
```

Replay protection: the verification result carries the matched time step, so accepting a code only once is one comparison — store the last accepted step per account (starting from `TotpVerifier.NO_MATCH`) and require a greater one:

```java
TotpVerification verification = totp.verifyCode(code, secret, Instant.now());
if (verification.isValid() && verification.timeStep() > lastAcceptedTimeStep) {
    lastAcceptedTimeStep = verification.timeStep(); // accept
} else {
    // invalid, or a replayed code
}
```

QR rendering requires the optional `totp-qr` module:

```java
TotpQrCodeGenerator qr = new TotpQrCodeGenerator();
byte[] png = qr.toPng(uri, 256);
```

## Spring

`Totp` is immutable and thread-safe, so it can be registered once as a singleton bean:

```java
@Configuration
public class TotpConfig {
    @Bean
    public Totp totp() {
        return Totp.builder()
                .digits(6)
                .periodSeconds(30)
                .algorithm(TotpAlgorithm.SHA1)
                .verificationWindow(1)
                .build();
    }
}
```

Inject it wherever codes are generated or verified:

```java
@Service
public class LoginService {
    private final Totp totp;

    public LoginService(Totp totp) {
        this.totp = totp;
    }

    public boolean verifyLogin(String secret, String code) {
        return totp.verifyCode(code, secret).isValid();
    }
}
```

The bean is stateless: pass each account's secret as an argument, and keep the replay-protection state per account in your own storage — the check-and-store must be atomic (see [SECURITY.md](SECURITY.md)). `TotpQrCodeGenerator` can be registered as a bean the same way.

## Configuration

Use `TotpSecretGenerator`, `TotpGenerator`, `TotpVerifier` directly when more control is needed.

## Build

```bash
./mvnw clean verify
```