# Stage 3c — Boot 3.3 checkpoint

Approved by owner 2026-09-04; GREEN / COMPLETE.
Scope: Boot parent/property 3.3.13, Springdoc webmvc-ui starter 2.6.0 and inherited
dependencies. Java 17 and Maven 3.9.16 unchanged. Only directly necessary fixes
are allowed; no business/schema redesign, next stage or remote push.

Reviewed Boot 3.3 release notes and Springdoc's Boot 3.3/2.6 compatibility matrix.
Flyway, Jersey and Prometheus migration changes do not apply to the inspected
dependency set. Runtime compatibility still requires tests and packaged startup.

Entry: repeat 46-test Boot 3.2 baseline. Verify default clean verify, the full
PostgreSQL suite against fresh grocery33_test copied from baseline schema only,
and packaged API/OpenAPI smoke. Use ddl-auto=validate, never baseline auto-update.
Stop on red/inconclusive behavior or schema changes. Rollback via reviewed stage
revert using untouched prior database/artifact. No database deletion authorized.

Sources accessed 2026-09-04:
- https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.3-Release-Notes
- https://docs.spring.io/spring-boot/3.3/appendix/dependency-versions/coordinates.html
- https://springdoc.org/v2/

## Results

Working directory: `/Users/ozkanogus/Projects/SpringBootSampleERP`; Temurin
17.0.20.1, Maven 3.9.16, macOS arm64, PostgreSQL 18.6.

The repeated Boot 3.2 baseline passed all 46 tests. Upgraded `./mvnw -B -ntp
-Pdev,postgres-tests clean verify` passed 46 tests (29 Surefire + 17 Failsafe),
zero failures/errors/skips, in 24.187 seconds. Default `./mvnw -B -ntp clean
verify` passed 30 tests in 6.282 seconds. Only three POM versions changed;
no source, configuration or test compatibility fixes were required.

Dependency tree: Spring 6.1.21, Hibernate 6.5.3.Final, Jackson 2.17.3,
PostgreSQL JDBC 42.7.7, H2 2.2.224 and Lombok 1.18.38.

Packaged startup used separate grocery33_smoke, copied from grocery_boot27
schema only, with ddl-auto=validate, private datasource environment overrides
and loopback port 18082. GET groceries/products/purchases/sales/stockMovements
returned 200 with 10/10/10/30/400 records; topSold/1 returned three records.
All five missing-resource routes returned empty 404; supplied-ID grocery POST
returned Problem status 400. OpenAPI returned 200 with 11 paths and the named
id path parameter. Column types/defaults/nullability and sequence increments
matched the baseline. No schema auto-update was used.

The application was stopped; new test/smoke databases remain, and earlier
databases were not modified. Local logs are
`/private/tmp/grocery33-{baseline,verify,default,smoke,dependencies}.log`.
Existing build/Logback warnings and coverage gaps remain. No comprehensive
OpenAPI diff, security audit, coverage measurement or deployment is claimed.
This historical migration bridge is not a deployment target. Next: separately
approved Boot 3.4 checkpoint; no remote push.
