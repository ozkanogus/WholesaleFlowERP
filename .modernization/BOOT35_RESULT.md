# Stage 3e — Boot 3.5 checkpoint

Owner approved 2026-09-04; GREEN / COMPLETE on `codex/boot-3-5`.
Scope: Boot parent/property 3.5.16, Springdoc 2.8.17 and inherited dependencies.
Java 17 and Maven 3.9.16 unchanged. Three manually reviewed version edits;
only directly required compatibility fixes allowed. No business changes,
schema migration, Boot 4 execution or remote push.

Rationale: complete the Boot 3 compatibility checkpoint before assessing Boot 4.
Boot 3.5.16 is the final OSS 3.5 release, not a deployment recommendation.
Springdoc's matrix pairs Boot 3.5 with 2.8.x; 2.8.17 was built on Boot 3.5.13.
Review covered stricter Boolean/profile settings, executor alias changes,
Jackson module retention, HikariCP and test-library updates. Runtime and tests
must verify actual compatibility; no custom taskExecutor references or
TestRestTemplate usage were found. Keep heapdump access disabled.

Entry: repeated Boot 3.4 PostgreSQL build passed in 9.003 seconds.
Verification: `./mvnw -B -ntp -Pdev,postgres-tests clean verify` using Java 17
and grocery35_test; default `./mvnw -B -ntp clean verify`; dependency tree;
packaged PostgreSQL/API/OpenAPI/Swagger UI smoke on grocery35_smoke with
ddl-auto=validate. New databases are schema-only copies of grocery_boot27.
Rollback: reviewed stage revert to prior main; preserve prior databases.
Stop on unexplained regressions; do not weaken tests.

Sources reviewed 2026-09-04:
- https://spring.io/blog/2026/06/25/spring-boot-3-5-16-available-now/
- https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.5-Release-Notes
- https://docs.spring.io/spring-boot/3.5/system-requirements.html
- https://springdoc.org/v2/
- https://github.com/springdoc/springdoc-openapi/releases/tag/v2.8.17

## Results

Working directory: `/Users/ozkanogus/Projects/SpringBootSampleERP`.
Environment: Temurin 17.0.20.1, Maven 3.9.16, macOS arm64, PostgreSQL 18.6.
Upgraded PostgreSQL build: 46 tests (29 Surefire + 17 Failsafe), zero
failures/errors/skips, 24.966 seconds. Default clean verify: 30 tests,
zero failures/errors/skips, 5.524 seconds. No source/test/configuration changes
or compatibility fixes were needed beyond the three POM version edits.

Resolved dependencies: Spring 6.2.19, Hibernate 6.6.53.Final, Jackson 2.21.4,
PostgreSQL JDBC 42.7.11, H2 2.3.232, Lombok 1.18.46.

Packaged startup used loopback port 18082, private datasource overrides and
ddl-auto=validate. GET groceries/products/purchases/sales/stockMovements
returned 200 with 10/10/10/30/400 records; topSold/1 returned three records.
Missing GET records returned empty 404 for all five resources. Minimal
{"id":-1} POST/PUT requests returned Problem status 400 for all five resources;
some responses are DTO-validation failures rather than existence-guard checks.
OpenAPI returned 200, 11 paths and the named id path parameter. Swagger UI HTML
returned 200. Column types/defaults/nullability and sequence increments matched
grocery_boot27. The app was stopped; the new databases remain. Prior database
schemas and data were not changed.

Existing build/Logback/MockBean warnings and documented coverage gaps remain.
No full OpenAPI schema diff, concurrency equivalence, security audit, measured
coverage or deployment is claimed. This completes Stage 3, not the whole
modernization. Before Boot 4, review server support, Jackson/error handling,
OpenAPI dependencies and test API removals; obtain separate execution approval.
Logs: `/private/tmp/grocery35-{baseline,verify,default,smoke,dependencies}.log`.
No remote push.
