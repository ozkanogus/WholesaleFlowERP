# Stage 3 entry proposal — Jakarta / Boot 3.1

Prepared 2026-09-04. Stage 3a approved and verified GREEN on 2026-09-04.
The proposal below is retained as the scoped decision record; execution evidence
and deviations are in `STAGE3_RESULT.md`. Later checkpoints remain unapproved.
Current baseline: Boot 2.7.18, Java 17, Maven 3.9.16; 46 verified tests.
No dependencies or production code were changed during this assessment.

## Proposed decision

Use **Spring Boot 3.1.12** as Stage 3a's first compatibility checkpoint, retaining
Java 17. This is an old engineering bridge, not a supported deployment target.
Review both the Boot 3.0 migration guide and 3.1 release notes during execution.
Subsequent 3.2, 3.3, 3.4 and 3.5 checkpoints need separately reviewed scopes.

Confirmed: Boot 3.0.13 manages Jackson 2.14.3 and lists Hibernate 5/Jakarta
adapters, but does not manage the Hibernate 6 adapter. Boot 3.1.12 manages
`jackson-datatype-hibernate6` with the rest of Jackson at 2.15.4. This repository
explicitly registers Hibernate5Module. Inference: entering at 3.1 avoids a
temporary unmanaged Jackson override or removal of that serialization support.
The tradeoff is including Hibernate 6.2 changes in the initial Jakarta step.
An isolated 3.0 checkpoint remains possible only after resolving its adapter
strategy; direct 3.5 or 4 would combine more changes and is deferred.

## Exact proposed dependency/configuration scope

| Component | Proposed action | Evidence status |
| --- | --- | --- |
| Boot parent and property | 3.1.12 together | Confirmed published dependency set |
| Java / Maven wrapper | Keep 17 / 3.9.16 | Existing local baseline; target build must run |
| Spring / Hibernate / PostgreSQL JDBC | Use Boot management; Hibernate 6.2.25.Final, JDBC 42.6.2 | Confirmed BOM; application compatibility unverified |
| Hibernate core and JPA processor | Change group to `org.hibernate.orm`, including annotationProcessorPaths | Confirmed target coordinates |
| Hibernate Jackson adapter | `com.fasterxml.jackson.datatype:jackson-datatype-hibernate6:2.15.4` via Boot; Hibernate6Module bean | Confirmed target artifact; JSON behavior must be tested |
| JAXB Jackson adapter | `jackson-module-jakarta-xmlbind-annotations`, Boot-managed | Confirmed target module; inspect usage before replacing |
| Annotation API | Replace javax.annotation dependency with Boot-managed jakarta.annotation-api | Jakarta dependency alignment |
| Validation/persistence imports | Change only javax.persistence and javax.validation references in main/test code | Confirmed local usage; never globally rename Java SE javax packages |
| JAXB processor runtime | Remove old 2.3.3 override and use Boot-managed JAXB runtime version | Inference: align processor with Jakarta; verify generation |
| Springdoc | `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0` | Official matrix pairs Boot 3.1 with 2.2.x; execution must verify resolution/OpenAPI |
| Problem Spring Web | `org.zalando:problem-spring-web:0.28.0` | Project documents >=0.28.0 for Spring 6; preserve 400/404 JSON tests |
| Lombok | Remove dependency's explicit 1.18.22; align dependency and processor to managed 1.18.32 | Confirmed BOM; generated builders need verification |
| MapStruct | Retain 1.4.2.Final initially | Compatibility inference, not established; stop if processor changes are needed |
| Naming | Replace removed SpringPhysicalNamingStrategy with Hibernate CamelCaseToUnderscoresNamingStrategy | Official deprecation replacement; compare actual schema |
| Dialect | Replace PostgreSQL82Dialect with PostgreSQLDialect in runtime and PostgreSQL tests | Hibernate 6 boundary; validate against existing schema |
| Server | Retain existing web/Undertow declarations; no server replacement | Cleanup and Boot 4 server decisions remain separate |
| Other plugins/dependencies | Keep explicit versions unless directly incompatible; record inherited plugin changes | No broad cleanup or Testcontainers redesign |

No OpenRewrite recipe is approved. Use a narrow reviewed patch for package and
coordinate changes; inspect generated code without committing generated output.

## Database and behavioral stop conditions

Do not run schema auto-update on either baseline database. Provision a new
`grocery_stage3_test` database from the verified baseline schema only, then use
the existing tests with `ddl-auto=validate`. Keep the Stage 2 database untouched.

Hibernate 6 changes identifier generation and temporal type handling. Explicitly
check `sequence_generator` name/increment, `Instant` timestamp mapping, numeric
precision, enum representation, composite IDs and orphan removal. If validation
fails, preserve the failure and propose a mapping compatibility change; do not
silently regenerate the schema or weaken tests. Business/schema redesign is out
of scope. A separate empty smoke database may be generated for comparison, but
that does not substitute for validating compatibility with the baseline schema.

Review these residual Stage 2 risks with the owner before execution:

- Multi-line aggregates and same-product quantity edits are not characterized.
- Successful HTTP writes and nested validation are not comprehensively tested.
- Operation-ID collision semantics across operation types remain unresolved.
- Full OpenAPI schema and lazy entity serialization are not covered by current
  DTO tests; add focused checks if the adapter transition exposes uncertainty.
- No CI, measured coverage, production deployment or data migration is established.

These are disclosed limitations, not claims of verified compatibility. Approval
must either accept this bounded checkpoint with these gaps or request more tests.

## Execution and exit contract (after approval only)

1. Branch from clean local main; repeat the 46-test Boot 2.7 baseline.
2. Apply only the scoped source/POM/configuration adaptation above. Inspect the
   effective dependency tree for accidental javax or Hibernate 5 runtime remnants.
3. Run `./mvnw clean verify` and `./mvnw -Pdev,postgres-tests clean verify` using
   the fresh baseline-schema test database and private environment credentials.
   Preserve all 46 assertions/discovered test cases; investigate any difference.
4. Run packaged startup and API/OpenAPI smoke checks on an isolated database;
   compare response fields/statuses and schema, not randomized demo quantities.
5. Record resolved versions, schema evidence, warnings and remaining gaps. Merge
   only green work locally; no push, deployment, or next minor step is authorized.

Rollback: retain the prior main commit/artifact and untouched baseline database;
use a reviewed code revert if necessary. Do not drop databases without approval.
Stop on required library replacement, unexplained schema/behavior changes, red
tests, or unresolved processor compatibility.

## Primary sources (accessed 2026-09-04)

- [Boot 3.0 migration guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)
- [Boot 3.1 release notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.1-Release-Notes)
- [Boot 3.0.13 dependency versions](https://docs.spring.io/spring-boot/docs/3.0.13/reference/html/dependency-versions.html)
- [Boot 3.1.12 dependency versions](https://docs.spring.io/spring-boot/docs/3.1.12/reference/html/dependency-versions.html)
- [Springdoc compatibility matrix](https://springdoc.org/v2/#what-is-the-compatibility-matrix-of-springdoc-openapi-with-spring-boot)
- [Problem Spring Web requirements](https://github.com/zalando/problem-spring-web)
- [Jackson Hibernate adapters](https://github.com/FasterXML/jackson-datatype-hibernate)
- [Hibernate 6 migration guide](https://docs.hibernate.org/orm/6.0/migration-guide/)
- [Boot naming strategy deprecation](https://docs.enterprise.spring.io/spring-boot/docs/2.7.25/api/org/springframework/boot/orm/jpa/hibernate/package-summary.html)
