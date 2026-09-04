# Java 21 migration — incomplete

2026-09-04. Owner approved execution after choosing Java 21 LTS.
Branch: codex/java21-upgrade. Status: RED / awaiting plugin-upgrade approval.
Main remains the verified Boot 4.0.8 / Java 17 application checkpoint.

## Completed

- Installed macOS ARM64 Temurin 21.0.12.1+1 alongside Java 17, with no global
  shell or OS Java selection change. JAVA_HOME for this stage:
  `/Users/ozkanogus/.local/opt/temurin-21`.
- Release metadata: https://api.adoptium.net/v3/assets/latest/21/hotspot?architecture=aarch64&image_type=jdk&os=mac&vendor=eclipse
- Pinned archive: OpenJDK21U-jdk_aarch64_mac_hotspot_21.0.12.1_1.tar.gz,
  release jdk-21.0.12.1+1 from adoptium/temurin21-binaries.
- SHA-256 verified against the published metadata:
  `3623232f33a9c3baadf304480b2535f9a3cba8a58d42ecbb438ba267315d9998`.
- Repeated Java 17 entry build: 56 tests pass, 11.505 seconds.
- JDK 21 with unchanged Java 17 compiler target: 56 tests pass, 11.048 seconds.
- Preserved Java 17 artifact starts on JDK 21 against isolated PostgreSQL with
  schema validation; groceries returns 10 records and OpenAPI is available.
  Process stopped after this preliminary runtime check.
- Changed only the POM Java target to 21. Compilation and all 39 Surefire tests
  pass. javap confirms application class-file major version 65.

## Blocker

`./mvnw -B -ntp -Pdev,postgres-tests clean verify` then fails at:

```text
org.gaul:modernizer-maven-plugin:2.3.0:modernizer
Unsupported class file major version 65
```

This is an introduced bytecode-tool compatibility failure, not a test assertion
failure. The release-17 build on the same JDK passes. No check was disabled and
no plugin/dependency was upgraded. The plan explicitly requires approval before
expanding to a dependency/tool upgrade, so execution stopped here.

Remaining gates: approve and assess a Java-21-compatible Modernizer version,
rerun both complete builds, inspect generated sources, and finish packaged
Java 21 runtime, schema and OpenAPI contract comparisons. The Java 21-targeted
Failsafe suite and final default build have NOT RUN. No merge or remote push of
the implementation. Do not treat the generated candidate JAR as verified.

Mockito dynamic-agent warnings occur on Java 21; tests pass and warnings were
not suppressed. Existing framework/model warnings remain separate debt.

## Evidence and recovery

Working directory: /Users/ozkanogus/Projects/SpringBootSampleERP.
Maven wrapper 3.9.16; local PostgreSQL 18.6, loopback port 55432.
Builds use explicit JAVA_HOME and private GROCERY_TEST_DB_* environment values.
New databases grocery21_test, grocery21_runtime17 and grocery21_smoke were
created from a schema-only copy of grocery_boot27; source data was not changed.
grocery21_smoke remains unused. All databases are retained.

Local logs: /private/tmp/java21-entry.log, java21-release17.log,
java21-runtime17.log and java21-final-postgres.log. Preserved rollback artifact:
/private/tmp/java21-entry.jar. These are local evidence, not durable CI results.
Java 17 remains installed. The incomplete implementation is isolated from main;
no application source, test assertion, schema or runtime configuration changed.
