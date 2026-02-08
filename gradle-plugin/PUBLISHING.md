# Publishing the Gradle Plugin

The Gradle plugin publishes to two locations:

1. **Maven Central** (via Central Portal - new way)
2. **Gradle Plugin Portal**

## Prerequisites

### 1. GPG Key for Signing

Generate a GPG key if you don't have one:

```bash
gpg --gen-key
```

List your keys:

```bash
gpg --list-keys
```

Export your public key to a keyserver:

```bash
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

### 2. Maven Central Portal Credentials

1. Create an account at https://central.sonatype.com/
2. Generate a user token:
    - Log in to Central Portal
    - Go to Account → Generate User Token
    - Save the username and password (these are your publishing credentials)

### 3. Gradle Plugin Portal Credentials

1. Create an account at https://plugins.gradle.org/
2. Generate API keys:
    - Log in to Gradle Plugin Portal
    - Go to your profile
    - Get your API Key and Secret

## Configuration

### Option 1: Environment Variables (Recommended for CI/CD)

```bash
export CENTRAL_USERNAME="your-central-token-username"
export CENTRAL_PASSWORD="your-central-token-password"
export GRADLE_PUBLISH_KEY="your-gradle-plugin-portal-api-key"
export GRADLE_PUBLISH_SECRET="your-gradle-plugin-portal-secret"
export MAVEN_GPG_PASSPHRASE="your-gpg-passphrase"
```

**Note:** The GPG key ID is configured in `build.gradle.kts`. If you need to use a different key, update the
`signing.gnupg.keyName` value in that file.

### Option 2: gradle.properties (Recommended for Local Development)

Create/edit `~/.gradle/gradle.properties`:

```properties
# Maven Central Portal (new way)
centralUsername=your-central-token-username
centralPassword=your-central-token-password
# Gradle Plugin Portal
gradle.publish.key=your-gradle-plugin-portal-api-key
gradle.publish.secret=your-gradle-plugin-portal-secret
# GPG Signing
signing.keyId=your-gpg-key-id-last-8-chars
signing.password=your-gpg-passphrase
signing.secretKeyRingFile=/Users/yourname/.gnupg/secring.gpg
```

**Note:** For GPG signing in Gradle 6+, you may need to export your secret key:

```bash
gpg --export-secret-keys YOUR_KEY_ID > ~/.gnupg/secring.gpg
```

Or use the Gradle in-memory key approach:

```properties
signing.gnupg.executable=gpg
signing.gnupg.useLegacyGpg=false
signing.gnupg.keyName=your-gpg-key-id
signing.gnupg.passphrase=your-gpg-passphrase
```

## Publishing

### From Maven (Recommended)

From the root of code-structure project:

```bash
# Publish everything including Gradle plugin
mvn deploy -Pstage
```

This will:

1. Build and deploy all Maven modules to Central Portal
2. Build the Gradle plugin
3. Publish Gradle plugin to Maven Central
4. Publish Gradle plugin to Gradle Plugin Portal

### From Gradle Directly

From the gradle-plugin directory:

```bash
# Publish to both Maven Central and Gradle Plugin Portal
./gradlew publishAllPublicationsToCentralRepository publishPlugins

# Or separately:
./gradlew publishAllPublicationsToCentralRepository  # Maven Central only
./gradlew publishPlugins                              # Gradle Plugin Portal only

# Publish to Maven Local for testing
./gradlew publishToMavenLocal
```

## Verification

### Check Maven Central

1. Go to https://central.sonatype.com/
2. Search for `com.seanshubin.code.structure`
3. Check that `code-structure-gradle-plugin` appears

**Note:** Maven Central can take 15-30 minutes to sync to Maven Central search

### Check Gradle Plugin Portal

1. Go to https://plugins.gradle.org/
2. Search for `com.seanshubin.code.structure`
3. Verify the plugin page shows the new version

**Note:** Gradle Plugin Portal usually updates within a few minutes

### Test the Published Plugin

Create a test project with:

```kotlin
plugins {
    id("com.seanshubin.code.structure") version "1.1.1"
}
```

Run:

```bash
./gradlew analyzeCodeStructure
```

## Troubleshooting

### "Could not find metadata"

The plugin hasn't synced yet. Wait 15-30 minutes for Maven Central.

### "Failed to sign"

Ensure GPG is set up correctly:

```bash
gpg --list-secret-keys
```

If using gpg-agent, make sure it's running:

```bash
gpg-agent --daemon
```

### "401 Unauthorized" for Maven Central

- Verify your Central Portal token username/password
- Tokens expire - generate a new one if needed
- Ensure you're using the Central Portal credentials, not OSSRH credentials

### "403 Forbidden" for Gradle Plugin Portal

- Verify your API key and secret
- Check that your account has publishing permissions
- API keys can be revoked - generate new ones if needed

## Security Notes

- **Never commit credentials** to version control
- Add `~/.gradle/gradle.properties` to `.gitignore` (already done globally)
- Use environment variables in CI/CD pipelines
- Rotate credentials periodically
- Use token-based authentication (not passwords) where available
