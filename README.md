# totp

A maintained TOTP implementation for the JVM.

It replaces unmaintained libraries such as `java-totp` and `GoogleAuth` and targets Java 17.

## Modules

- `totp-core` - pure Java implementation of RFC 4226 (HOTP) and RFC 6238 (TOTP)
- `totp-qr` - optional QR code generation module using ZXing

## Features

- Generate cryptographically secure Base32 secrets
- Generate TOTP codes (SHA-1, SHA-256, SHA-512)
- Verify codes with a configurable time window
- Build `otpauth://` URIs for authenticator apps
- RFC 6238 test vectors verified
- JSpecify nullability annotations
- Fluent `Totp` facade for simple usage

## Installation

### Gradle

```gradle
dependencies {
    implementation("io.github.allbegray:totp-core:1.0.2")
}
```

Optional QR support:

```gradle
dependencies {
    implementation("io.github.allbegray:totp-core:1.0.2")
    implementation("io.github.allbegray:totp-qr:1.0.2")
}
```

### Maven

```xml
<dependency>
    <groupId>io.github.allbegray</groupId>
    <artifactId>totp-core</artifactId>
    <version>1.0.2</version>
</dependency>
```

Optional QR support:

```xml
<dependency>
    <groupId>io.github.allbegray</groupId>
    <artifactId>totp-qr</artifactId>
    <version>1.0.2</version>
</dependency>
```

## Usage

```java
Totp totp = Totp.defaults();

String secret = totp.generateSecret();
String code = totp.generateCode(secret);
boolean valid = totp.verifyCode(code, secret);
String uri = totp.otpAuthUri("Example", account, secret);
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

QR rendering requires the optional `totp-qr` module and ZXing:

```java
TotpQrCodeGenerator qr = new TotpQrCodeGenerator();
byte[] png = qr.toPng(uri, 256);
```

## Configuration

Use `TotpSecretGenerator`, `TotpGenerator`, `TotpVerifier` directly when more control is needed.

## Build

```bash
./gradlew build
```