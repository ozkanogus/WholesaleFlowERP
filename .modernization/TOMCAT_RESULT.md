# Stage 4b — Tomcat standardization

Owner approved 2026-09-04; GREEN. Branch: `codex/tomcat-standardization`.
Boot 3.5.16, Springdoc 2.8.17 and Java 17 unchanged. Scope: remove the
redundant Undertow starter and add a real-server regression test. No business,
JSON/error-library, schema, framework-version or deployment changes.

## Baseline and implementation

Repeated the 51-case PostgreSQL suite before edits: passed in 10.257 seconds.
Before removing the starter, a random-port H2-backed application identified
TomcatServletWebServerFactory and TomcatWebServer and served an empty HTTP 404
for /api/groceries/-1. Thus this is dependency cleanup, not a change of active
server. Initial diagnostic prints were replaced with explicit type assertions.

WebServerTest uses loopback, bounded HTTP timeouts, a dedicated H2 database and
MockitoBean(enforceOverride=true) only for demo startup. DirtiesContext closes
the embedded server after the test class. The retained test checks the actual
server object as well as its factory and a real HTTP exchange.

Removed only spring-boot-starter-undertow from the POM. Dependency tree and
packaged BOOT-INF/lib inspection show no Undertow; Tomcat 10.1.55 remains.
Dormant Undertow logger settings were left untouched as unrelated cleanup.

## Verification

Working directory: `/Users/ozkanogus/Projects/SpringBootSampleERP`.
Environment: Temurin 17.0.20.1, Maven 3.9.16, macOS arm64, PostgreSQL 18.6.

- `./mvnw -B -ntp -Dtest=WebServerTest test`: pre-removal server observation passed.
- `./mvnw -B -ntp -Pdev,postgres-tests clean verify`: 52 cases
  (35 Surefire + 17 Failsafe), zero failures/errors/skips, 10.712 seconds.
- `./mvnw -B -ntp clean verify`: 36 cases passed, zero failures/errors/skips.
- Packaged smoke on separate grocery4b_smoke (schema-only copy of grocery_boot27),
  loopback port 18082, private datasource overrides and ddl-auto=validate passed.
  Lists returned 10/10/10/30/400 groceries/products/purchases/sales/stock movements;
  topSold/1 returned three. Five missing-resource GETs returned empty 404.
  Otherwise-valid grocery supplied-ID POST and absent/unknown-ID PUT returned
  the exact custom title/status objects with application/problem+json.
  OpenAPI returned 11 paths and the id parameter; Swagger UI HTML returned 200.
  Columns/types/defaults/nullability and sequence increments matched baseline.

The app was stopped. The new smoke database remains; prior baseline databases
were not changed. Automated tests used grocery35_test with schema validation.
Logs: `/private/tmp/grocery4b-{baseline,server-before,verify,dependencies,smoke,default}.log`.
No measured coverage, full OpenAPI diff, load test or production readiness claim.

Rollback: reviewed revert of this isolated stage, repeat prior verification;
no database restoration is required because no schema migration occurred.
Next gate: propose the native error-adapter replacement with Stage 4a contracts
preserved. Do not execute it or Boot 4 without approval. No remote push.
