# Publishing

Both artifacts (`forminput` and `forminput-views`) go to Maven Central under `io.github.omarshehe`.

1. Put these in `~/.gradle/gradle.properties` (never in the repo): `mavenCentralUsername`, `mavenCentralPassword` (a user token from the Central Portal), `signingInMemoryKey`, `signingInMemoryKeyId`, `signingInMemoryKeyPassword`. The public half of the signing key must be on a keyserver such as `keys.openpgp.org`.
2. From the commit to release (tagged, CI green): `./gradlew publishToMavenCentral`. The iOS artifacts need a macOS machine. When only one artifact changed, publish just that one (`./gradlew :forminput:publishToMavenCentral`): Central rejects a version that is already published, so the unscoped task fails on the artifact that did not change. The versions are separate (`composeVersion` for `forminput`, `versionName` for `forminput-views` in `gradle/libs.versions.toml`).
3. In the Central Portal, open **Deployments**, wait for *Validated*, and click **Publish**. A published release cannot be changed or deleted.

The Publish step also works without the web page, using the same token Gradle used (`Authorization: Bearer base64(mavenCentralUsername:mavenCentralPassword)`). The upload prints a deployment id; then:

- `POST https://central.sonatype.com/api/v1/publisher/status?id=<id>` returns `deploymentState` (`VALIDATED` when it can be published) and the list of artifacts.
- `POST https://central.sonatype.com/api/v1/publisher/deployment/<id>` publishes it (HTTP 204). The state goes `PUBLISHING`, then `PUBLISHED`, which can take a few minutes to half an hour.

For a local build to use from another project: `./gradlew :forminput:publishToMavenLocal`.

CI (`.github/workflows/ci.yml`) runs the desktop tests and the Android compile on Linux and the iOS compile on macOS for every pull request and every push to `master`.
