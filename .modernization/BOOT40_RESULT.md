# Stage 4d — Boot 4.0 checkpoint

2026-09-04. Owner approved BOOT4_TRANSITION_PLAN.md. Implementation verified on
codex/boot4-transition; no remote push or deployment.

## Result

Boot 4.0.8 / Springdoc 3.0.3 now builds and runs with Java 17 unchanged.
The managed stack includes Framework 7.0.9, Hibernate 7.2.24.Final,
Jackson 3.1.5, Tomcat 11.0.24, PostgreSQL JDBC 42.7.13 and JUnit Jupiter 6.0.3.
MapStruct remains 1.4.2.Final; Lombok remains 1.18.46.

Changes are limited to the approved framework boundary:

- MVC/AspectJ starters and MVC/JPA test starters.
- Hibernate processor coordinates in all three existing declarations.
- Jackson 3 modules/builders/converters; integrated Java-time/JDK8 support.
- Boot test/server package moves and four object-equality JUnit imports.
- Removal of inert LiquibaseProperties registration, without installing Liquibase.
- Naming-strategy class relocation in main/test properties.
- Explicit matching Boot version for the now-unmanaged loader-tools dependency.

No controller, service, entity, repository query or error-advice behavior changed.
No tests were weakened. One context test was added to assert the actual MVC
Jackson 3 mapper, Hibernate 7 module registration and absence of a Jackson 2
ObjectMapper application bean.

## Verification evidence

Working directory: /Users/ozkanogus/Projects/SpringBootSampleERP.
Temurin 17.0.20.1, Maven wrapper 3.9.16, local PostgreSQL 18.6.

| Check | Measured outcome |
| --- | --- |
| Unchanged Boot 3.5 entry build | 55 pass; 10.364 s |
| Focused migrated MVC/context/mapping tests | 15 pass before additional wiring test |
| ./mvnw -B -ntp -Pdev,postgres-tests clean verify | 56 pass: 39 Surefire + 17 Failsafe; 9.844 s |
| ./mvnw -B -ntp clean verify | 40 pass: 39 Surefire + 1 Failsafe; 6.621 s |
| Final failures / errors / skips | 0 / 0 / 0 |
| PostgreSQL test database | grocery40_test; schema-only copy of grocery_boot27, validation |
| Packaged database | grocery40_smoke; same source schema, validation, loopback 18082 |
| OpenAPI | All 11 path definitions and component schemas equal to Stage 4c after key sorting |
| Schema comparison | Columns/types/defaults/nullability, constraints and sequence increments equal |

Packaged HTTP checks: 10 groceries, 10 products, 10 purchases, 30 sales and
400 stock movements; five empty missing-ID 404s; three top-sold report rows;
Swagger UI; business 400 titles, malformed-parser prefix and five validation
violations under both JSON Accept variants. Purchase/sale timestamps remain
ISO-like strings; deterministic DTO timestamp/number assertions remain in tests.
SQL confirms 100 positive PURCHASE and 300 negative SALE stock rows.
The 500 redaction contract remains covered by the unchanged synthetic advice test.

Generated MapStruct implementations and JPA metamodel files were inspected,
including PurchaseMapperImpl and PurchaseProduct_ type references. No hand edits.
The existing packaged entry-point test and real Tomcat test pass.
The smoke process was stopped; all earlier databases remain untouched.

## Entry review and diagnostics

The Spring Data documentation gap was resolved using the maintained
[4.0.0 release notes](https://github.com/spring-projects/spring-data-jpa/releases/tag/4.0.0),
[4.0.7 release notes](https://github.com/spring-projects/spring-data-jpa/releases/tag/4.0.7)
and direct 4.0.7 JpaRepository/Query API inspection. The application's derived
queries, native report and transaction workflows pass on the candidate.
This is application evidence, not a universal compatibility claim.

Three introduced build issues were corrected within scope:

1. The BOM no longer supplies loader-tools' version: pin to spring-boot.version.
2. Removed/moved test imports: use candidate-JAR paths and Jupiter assertions.
3. Configured SpringImplicitNamingStrategy moved to org.springframework.boot.hibernate.
   Preserve the strategy rather than deleting the explicit naming configuration.

These are distinct from the Stage 4c IDE/compiler interference, which did not
recur in this stage. No entity/schema changes were needed.

## Dependency and remaining-risk review

Jackson 2.21.5 remains transitively through Springdoc 3.0.3 ->
swagger-core-jakarta 2.2.47. It is not a newly enabled Jackson 2 application
bridge. Both JSON versions are therefore packaged; the new context test verifies
MVC uses Jackson 3. No broad dependency exclusion was applied.

The executable JAR still contains the provided Hibernate processor, just as the
baseline packaged its predecessor. Existing loader/build dependency cleanup is
deferred, not silently described as absent. No Zalando adapter was reintroduced.

Existing plugin warnings, configuration credentials, demo seeding and default
ddl-auto=update remain development debt. No CI, deployment/security assessment,
coverage percentage, exhaustive lazy-association contract or performance claim.
Boot 4.1, a JDK upgrade, schema migrations and business changes are separate work.

Rollback: reviewed revert of this isolated stage and rerun the Boot 3.5 baseline;
retain earlier databases. The pre-stage artifact is /private/tmp/grocery40-baseline.jar.

Local logs: /private/tmp/grocery40-baseline.log, grocery40-compile.log,
grocery40-test-compile.log, grocery40-focused.log, grocery40-focused2.log,
grocery40-final-verify.log, grocery40-final-default.log, grocery40-smoke.log
and grocery40-dependencies.log. OpenAPI capture: /private/tmp/grocery40-openapi.json.
The final reruns include the extra wiring test; runtime production sources are
identical to those used for the packaged smoke. Logs are local, not durable CI evidence.
