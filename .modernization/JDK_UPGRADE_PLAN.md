# JDK upgrade assessment

Prepared: 2026-09-04. Status: GREEN / COMPLETE, including the owner-approved
Modernizer upgrade. See [JAVA21_RESULT.md](JAVA21_RESULT.md). The assessment and
proposed gates below are retained as history; Java 21 verification now passes.

## Selected target

Move from Temurin 17 to Temurin 21 LTS in a separate, verified stage, following
the owner's preference. Java 25 is deferred, not a required subsequent stage.
Java 21 is within the current framework's supported range; application-level
compatibility remains subject to the verification gates below.

Spring Framework 7 recommends JDK 25 while retaining a Java 17 baseline.
Adoptium lists availability through at least September 2031 for Java 25 and
December 2029 for Java 21. These are roadmap dates, not a commercial support SLA.
Select and record an exact current macOS ARM64 Temurin patch and verify its
published checksum at execution time; do not use an unrecorded floating download.

## Evidence and remaining uncertainty

The verified baseline is Boot 4.0.8 / Java 17 at merge ee68e70. Existing evidence
records 40 default tests and 56 PostgreSQL-profile tests passing, plus packaged
runtime checks; see [BOOT40_RESULT.md](BOOT40_RESULT.md). These were not rerun
for this documentation-only assessment. Only Java 17 is installed locally.

| Component | Assessment |
| --- | --- |
| Boot 4.0.8 | Its tagged requirements support Java 17 through 26 and Maven 3.6.3+; both proposed LTS versions are in range |
| Lombok 1.18.46 | Changelog introduced Java 25 support in 1.18.40; no speculative upgrade needed |
| Byte Buddy 1.17.8 | Official compatibility table lists Java 25+ support from 1.17.0; Mockito behavior still requires execution |
| MapStruct 1.4.2.Final | Old processor remains an explicit uncertainty; clean annotation processing and generated mappings must pass |
| Hibernate processor / Maven plugins | Current managed versions and Maven 3.9.16 are identified, but this application's Java 21 build is untested |
| Deployment | No established CI, container or production runtime imposes a Java 21 constraint |

Framework compatibility is not proof of application compatibility. Java 21 and
25 application tests remain NOT RUN. No performance improvement is claimed.

## Bounded execution after approval

1. Start a focused `codex/` implementation branch from clean local main. Preserve
   the verified Java 17 artifact and baseline evidence. Do not push.
2. Install pinned, checksum-verified Temurin 21 alongside Java 17. Use explicit
   per-command JAVA_HOME; do not change global shell or OS Java selection.
3. With the compiler release still 17, run the existing build on JDK 21 and smoke
   the preserved Java 17 artifact on that runtime. This separates runtime and
   build-tool failures from a change in the emitted class-file version.
4. Change the POM Java target to 21, retaining Boot and dependency versions.
   Inspect effective compiler release and generated MapStruct/JPA sources.
5. Run `./mvnw clean verify` and `./mvnw -Pdev,postgres-tests clean verify` using
   an isolated schema-only PostgreSQL database and private environment settings.
   Require all existing assertions and no unexpected skips.
6. Smoke the packaged Java 21 artifact with schema validation, compare schema,
   OpenAPI and HTTP/error contracts, and exercise the established workflows.
7. Record versions, commands, warnings, test totals and runtime results. Update
   README/toolchain guidance and modernization records before proposing a merge.

Keep IDEs from modifying Maven's target directory during verification. Inspect
Mockito/agent warnings rather than broadly suppressing them. Stop on unexplained
failures, processor incompatibility, contract drift or a required dependency
upgrade; propose any additional change explicitly instead of widening this stage.

Out of scope: virtual threads, preview features, language refactors, GC tuning,
framework upgrades, business changes, schema changes, CI and deployment setup.

## Stopping point and rollback

The verified Java 17 / Boot 4 checkpoint remains the stopping point until every
gate passes. Keep Java 17 installed. If implementation fails, retain evidence
on the feature branch; do not merge. After a merge, use a reviewed revert and
rebuild with Java 17. A Java 21-targeted artifact cannot be rolled back merely
by selecting a Java 17 runtime. Retain the preceding artifact and avoid schema
auto-update throughout the comparison.

## Primary sources checked 2026-09-04

- [Boot 4.0.8 tagged system requirements](https://github.com/spring-projects/spring-boot/blob/v4.0.8/documentation/spring-boot-docs/src/docs/antora/modules/ROOT/pages/system-requirements.adoc)
- [Spring Framework 7 release notes](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-7.0-Release-Notes)
- [Adoptium support roadmap](https://adoptium.net/support/)
- [Lombok changelog](https://projectlombok.org/changelog)
- [Byte Buddy Java compatibility](https://github.com/raphw/byte-buddy#java-version-compatibility)
