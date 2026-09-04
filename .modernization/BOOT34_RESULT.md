# Stage 3d — Boot 3.4 checkpoint

Owner approved 2026-09-04; GREEN / COMPLETE on `codex/boot-3-4`.
Scope: Boot parent/property 3.4.13, Springdoc 2.7.0 (within its Boot 3.4
compatibility matrix), inherited dependencies and directly required fixes.
Java 17 and Maven 3.9.16 stay unchanged. Manual three-version patch is smaller
than introducing a transformation tool. No business redesign, schema migration,
next framework stage or remote push is authorized.

Boot 3.4 is an intermediate migration checkpoint, not a deployment target;
its open-source support has ended. Review identified Hibernate 6.6 merge
semantics as a risk for generated-ID entities with missing rows, plus H2,
WebJars and web shutdown changes. Existing workflow tests and packaged smoke
must pass. MockBean deprecation is recorded, not removed in this stage.
Explicit PostgreSQL test database replacement NONE is retained. No application
RestClient/RestTemplate or custom validated configuration properties were found.

Entry baseline: Boot 3.3 full PostgreSQL build passed in 8.748 seconds.
Verification: Java 17 `./mvnw -B -ntp -Pdev,postgres-tests clean verify` with
fresh grocery34_test, then packaged smoke with grocery34_smoke and
ddl-auto=validate; both use schema-only copies from grocery_boot27. Also run
default `./mvnw -B -ntp clean verify` and inspect resolved dependencies.
Rollback is a reviewed stage revert to the prior local main; prior database
schemas remain untouched. Stop on unexplained failures or behavior changes.

Sources reviewed 2026-09-04:
- https://spring.io/blog/2025/12/18/spring-boot-3-4-13-available-now/
- https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.4-Release-Notes
- https://docs.spring.io/spring-boot/3.4/system-requirements.html
- https://springdoc.org/v2/
- https://github.com/springdoc/springdoc-openapi/releases/tag/v2.7.0
- https://docs.hibernate.org/orm/6.6/migration-guide/

## Results

Environment: Temurin 17.0.20.1, Maven 3.9.16, macOS arm64, PostgreSQL 18.6.
Working directory: `/Users/ozkanogus/Projects/SpringBootSampleERP`.
The upgraded PostgreSQL build passed 46 tests (29 Surefire + 17 Failsafe),
zero failures/errors/skips, in 25.938 seconds. Default clean verify passed
30 tests in 5.842 seconds. No source, test or configuration edits were needed.

Resolved dependencies: Spring 6.2.15, Hibernate 6.6.39.Final, Jackson 2.18.5,
PostgreSQL JDBC 42.7.8, H2 2.3.232 and Lombok 1.18.42.

Packaged startup on loopback port 18082 with private datasource overrides and
ddl-auto=validate passed. GET groceries/products/purchases/sales/stockMovements
returned 200 with 10/10/10/30/400 records; topSold/1 returned three. Missing GET
records returned empty 404 for all five resources. POST and PUT with the
minimal payload {"id":-1} returned Problem status 400 for each resource;
some responses arise from DTO validation, not the existence guard. All five
controllers also retain explicit existsById checks before updates. Concurrent
deletion between that check and save is not covered and may expose Hibernate's
new optimistic-lock behavior; no concurrency equivalence is claimed.

OpenAPI returned 200 with 11 paths and the named id path parameter; Swagger UI
HTML returned 200. Column types/defaults/nullability and sequence increments
matched grocery_boot27. The app was stopped. New databases remain; no prior
database schema or data was modified.

New MockBean removal warnings are deferred to a focused test-API cleanup before
Boot 4. Existing build and Logback warnings remain. No full OpenAPI diff,
security audit, coverage measurement, concurrency test or deployment is claimed.
Logs: `/private/tmp/grocery34-{baseline,verify,default,smoke,dependencies}.log`.
Next: separately approved Boot 3.5 checkpoint. Nothing is pushed remotely.
