# Java 21 migration — verified

2026-09-04. Owner approved execution after choosing Java 21 LTS.
Branch: codex/java21-upgrade. Status: GREEN / COMPLETE.
Owner subsequently approved upgrading Modernizer and completing verification.

## Final result

Temurin 21.0.12.1+1 / compiler release 21 / Boot 4.0.8 passes the approved gates.
Modernizer 2.3.0 -> 2.7.0 is the only dependency/plugin upgrade. Its official
[release notes](https://github.com/gaul/modernizer-maven-plugin/releases/tag/modernizer-maven-plugin-2.7.0)
explicitly add ASM 9.5 for Java 21 compatibility. Selected as a bounded
compatibility fix, not a claim that 2.7.0 is the latest release.

The updated rule set flagged one guarded Optional.get() in SaleService's report
mapping. Replaced it with orElseThrow() under the unchanged isPresent() guard:
present products are still added; missing products are still omitted. No rule
exclusions, skipped checks, test changes or business-contract changes.

| Final check | Result |
| --- | --- |
| ./mvnw -B -ntp -Pdev,postgres-tests clean verify | 56 pass (39 Surefire + 17 Failsafe), 10.644 s |
| ./mvnw -B -ntp clean verify | 40 pass (39 Surefire + 1 Failsafe), 7.244 s |
| Failures / errors / skips | 0 / 0 / 0 in both builds |
| Modernizer 2.7.0 | Pass in both builds, no exclusions |
| Compiler | Main and test release 21; javap application major version 65 |
| Generated sources | Existing MapStruct 1.4.2.Final mappings and Hibernate metamodel compile; PurchaseMapperImpl and PurchaseProduct_ inspected |
| Packaged runtime | Starts on Java 21 against grocery21_smoke with ddl-auto=validate |
| OpenAPI | All path definitions and component schemas equal to Boot 4 / Java 17 capture after key sorting |
| Database schema | Before/after schema-only pg_dump equal after removing randomized dump restrict tokens |

Runtime HTTP checks pass: list counts 10 groceries, 10 products, 10 purchases,
30 sales and 400 stock movements; five empty missing-ID 404s; three report rows;
Swagger UI; existing-ID business 400, malformed JSON 400 and validation 400 under
both JSON Accept variants. SQL verifies 100 positive PURCHASE stock rows and
300 negative SALE rows. The automated PostgreSQL suite covers deterministic
reporting, transaction rollback and purchase/sale workflows. Smoke process stopped.

Final logs: /private/tmp/java21-fixed-postgres.log, java21-fixed-default.log,
java21-final-smoke.log. OpenAPI: /private/tmp/java21-final-openapi.json.
Schema dumps: /private/tmp/java21-before.sql and java21-after.sql.
The intermediate Modernizer rule failure remains in java21-modernizer-postgres.log.

No CI/deployment or coverage percentage was measured. Mockito dynamic-agent
warnings and existing configuration/security debt remain; no warnings suppressed.
Java 17 is retained, but cannot execute the release-21 artifact. Rollback requires
the preceding artifact or a reviewed revert and Java 17 rebuild. Java 25, broad
dependency cleanup, virtual threads and business redesign remain out of scope.

## Initial attempt history (superseded by the green result above)

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

Working directory: /Users/ozkanogus/Projects/WholesaleFlowERP.
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
