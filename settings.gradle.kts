rootProject.name = "totp"

include("totp-core")
include("totp-qr")

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}