# Stage 3a result — Boot 3.1.12 / Jakarta

2026-09-04: GREEN. Owner approved the scoped checkpoint and disclosed test gaps.
Java 17 and Maven 3.9.16 are unchanged. No subsequent minor checkpoint executed.

## Changes and diagnostics

- Boot parent/property 3.1.12; persistence and validation packages migrated in
  production and tests. No business methods or test expectations changed.
- Hibernate coordinates moved to org.hibernate.orm; managed Hibernate 6.2.25.Final,
  Jackson 2.15.4 Hibernate6Module and Jakarta JAXB adapter, JAXB runtime 4.0.5,
  Lombok 1.18.32. MapStruct 1.4.2.Final remained compatible in this build.
- Springdoc starter-webmvc-ui 2.2.0 and Problem Spring Web 0.28.0.
- Replaced obsolete physical naming strategy and PostgreSQL82Dialect in runtime
  and test configuration. No schema rewrite or timestamp compatibility override.
- First compile failed because ProblemModule is no longer a transitive compile
  dependency. Added jackson-datatype-problem 0.27.1 explicitly, matching the
  downloaded problem-spring-parent 0.28.0 POM's problem.version.
- Next run found three context errors due to legacy test Logback `<level>`
  syntax. Changed to `<root level="WARN">`, preserving the configured level.
  The standalone Jackson test now calls the renamed Hibernate6Module bean.

These were in-scope compatibility corrections, not weakened assertions or new
business behavior. Runtime dependency-tree inspection found no javax artifacts
or Hibernate 5 runtime dependencies. This is not a security vulnerability audit.

## Verification

Working directory: `/Users/ozkanogus/Projects/SpringBootSampleERP`.
Temurin 17.0.20.1, Maven 3.9.16, macOS arm64, PostgreSQL 18.6.

- Before changes: `./mvnw -Pdev,postgres-tests clean verify`: 46 passed.
- After changes: same command: 46 passed, 29 Surefire + 17 Failsafe, no skips.
- Default `./mvnw clean verify`: 30 passed. Packaging check passed.
- `./mvnw dependency:tree`: successful; resolved runtime reviewed.

For PostgreSQL tests set GROCERY_TEST_DB_URL to
`jdbc:postgresql://127.0.0.1:55432/grocery_stage3_test` and the existing private
user/password environment variables. This fresh database was populated with
schema-only output from grocery_boot27. Tests retained ddl-auto=validate.

Packaged startup used a separate fresh schema-only database grocery_stage3_smoke,
SPRING_JPA_HIBERNATE_DDL_AUTO=validate and loopback port 18082. Existing demo
seeding ran unchanged. GET groceries/products/purchases/sales/stockMovements
returned 200 with 10/10/10/30/400 rows; topSold/1 returned three products.
GET /v3/api-docs returned 200 and 11 paths. Full-context automated 400/404 and
DTO tests also passed. The process was stopped after checks.

Copied schema columns (type/default/nullability) and sequence increments remain
identical to the Boot 2.7 baseline after smoke startup. No baseline database was
updated; the two new databases remain. This validates the tested mappings against
the old schema, not compatibility of every Hibernate-generated DDL statement.

## Remaining limits

The accepted Stage 2 coverage gaps remain. No full OpenAPI schema diff, lazy
entity serialization audit, network HTTP write suite, deployment or coverage
measurement was performed. Framework-default changes outside characterized
contracts, such as trailing-slash routing, have not been exhaustively compared.
Boot 3.1.12 is an intermediate bridge, not an approved production destination.

Existing missing properties-plugin version/resources warnings remain. Boot now
warns about the obsolete repackage `fork` setting, and Logback warns that
DelayingShutdownHook was renamed. These did not prevent verification; cleanup
was deferred rather than folded into the migration.

Local diagnostic logs: /private/tmp/grocery-jakarta-{baseline,first,second,third,
default,smoke,dependencies}.log. Logs are temporary, not committed evidence files.
Next: separately assess the Boot 3.2 checkpoint. No remote push is authorized.
