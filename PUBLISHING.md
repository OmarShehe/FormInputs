# Publishing

Both artifacts (`forminput` and `forminput-views`) go to Maven Central under `io.github.omarshehe`.

1. Put these in `~/.gradle/gradle.properties` (never in the repo): `mavenCentralUsername`, `mavenCentralPassword` (a user token from the Central Portal), `signingInMemoryKey`, `signingInMemoryKeyId`, `signingInMemoryKeyPassword`. The public half of the signing key must be on a keyserver such as `keys.openpgp.org`.
2. From the commit to release (tagged, CI green): `./gradlew publishToMavenCentral`. The iOS artifacts need a macOS machine.
3. In the Central Portal, open **Deployments**, wait for *Validated*, and click **Publish**. A published release cannot be changed or deleted.

For a local build to use from another project: `./gradlew :forminput:publishToMavenLocal`.

CI (`.github/workflows/ci.yml`) runs the desktop tests and the Android compile on Linux and the iOS compile on macOS for every pull request and every push to `master`.
