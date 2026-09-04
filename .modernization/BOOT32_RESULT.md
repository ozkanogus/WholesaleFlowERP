# Stage 3b — Boot 3.2 checkpoint

Approved by owner 2026-09-04; GREEN / COMPLETE.
Scope: Boot parent/property 3.2.12, Springdoc webmvc-ui starter 2.5.0,
inherited managed dependencies, and only necessary compatibility fixes.
Java 17, Maven wrapper and business behavior stay unchanged. No next stage or
remote push is authorized. This is a migration bridge, not a deployment target.

Compatibility review: Boot 3.2 changes parameter-name discovery, its JAR launcher
and H2 version. Verify inherited compiler parameter metadata, packaged startup,
and fresh in-memory H2 tests. Springdoc's matrix pairs Boot 3.2 with 2.3–2.5.
Keep other explicit library versions unless a directly related failure requires
a scoped correction; stop before business/schema redesign or library replacement.

Entry baseline: 46 Boot 3.1 tests. Verification: default clean verify and the
46-case PostgreSQL build, then packaged API/OpenAPI smoke with ddl-auto=validate.
Fresh grocery32_test database uses schema-only output from grocery_boot27.
Never auto-update the baseline schema. Rollback is a reviewed stage revert using
the untouched prior database/artifact; no database deletion without approval.

Sources reviewed 2026-09-04:
- https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.2-Release-Notes
- https://docs.spring.io/spring-boot/docs/3.2.12/reference/html/dependency-versions.html
- https://springdoc.org/v2/

## Results

Working directory: `/Users/ozkanogus/Projects/SpringBootSampleERP`; Temurin
17.0.20.1, Maven 3.9.16, macOS arm64, PostgreSQL 18.6.

The 46-test Boot 3.1 baseline passed. Upgraded `./mvnw -B -ntp
-Pdev,postgres-tests clean verify` passed all 46 (29 Surefire + 17 Failsafe),
zero failures/errors/skips, in 22.234 seconds. Default `./mvnw -B -ntp clean
verify` also passed its 30 tests. No source, configuration or test changes were
needed; only parent, Boot property and Springdoc versions changed.

Dependency tree: Spring 6.1.15, Hibernate 6.4.10.Final, Jackson 2.15.4,
PostgreSQL JDBC 42.6.2, H2 2.2.224, Lombok 1.18.36. No javax runtime artifacts
were reported. Parent enables parameters; javap confirmed MethodParameters on
GroceryResource. The new packaged launcher worked without a fallback.

Packaged smoke ran against separate grocery32_smoke, copied from baseline
schema only, with SPRING_JPA_HIBERNATE_DDL_AUTO=validate, private datasource
environment overrides, SERVER_ADDRESS=127.0.0.1 and SERVER_PORT=18082.
GET groceries/products/purchases/sales/stockMovements returned 200 and
10/10/10/30/400 records; topSold/1 returned three. All five missing-resource
routes returned empty 404; supplied-ID grocery create returned Problem status
400. OpenAPI returned 200, 11 paths, and retained the named id path parameter.
Columns/types/defaults/nullability and sequence increments matched the baseline.

The application was stopped. New databases remain; earlier databases were not
modified. Logs: /private/tmp/grocery32-{baseline,verify,default,smoke,dependencies}.log.

Existing properties-plugin/resources/fork/Logback warnings and previously
disclosed coverage gaps remain. No comprehensive OpenAPI diff, security audit,
coverage measurement or deployment is claimed. Next: separately authorized
Boot 3.3 checkpoint. Do not deploy this historical migration bridge.
