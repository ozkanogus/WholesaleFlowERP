# Stage 4d — Boot 4 transition proposal

Prepared 2026-09-04 against main 749677c. Subsequently approved and verified;
see BOOT40_RESULT.md. The original scope and entry evidence follow.
Current evidence: 55 PostgreSQL-profile / 39 default tests pass on Boot 3.5.16.
No new test result is claimed by this documentation change.

## Recommendation

Use Boot 4.0.8 with Springdoc 3.0.3, retaining Java 17 and Maven 3.9.16.
This is a bounded major-version checkpoint, not deployment approval. Published
metadata confirms both versions; Springdoc's matrix pairs Boot 4.0.x with 3.0.x.
Its 3.0.3 parent uses Boot 4.0.5: compatibility with 4.0.8 is a reasoned candidate,
not an application-tested fact.

Boot 4.1.1 exists but adds a minor-line transition; defer it until this checkpoint
is green. Remaining on the verified 3.5.16 checkout is the rollback/stopping point,
not a claim of ongoing free support or production readiness.

## Confirmed managed versions

Read from the published Boot 4.0.8 BOM, not overridden individually:

| Component | Candidate managed version |
| --- | --- |
| Spring Framework | 7.0.9 |
| Spring Data BOM / JPA | 2025.1.7 / 4.0.7 |
| Hibernate ORM | 7.2.24.Final |
| Jackson BOM | 3.1.5 |
| Jackson 2 compatibility BOM | 2.21.5 (available, not the preferred application mapper) |
| Tomcat | 11.0.24 |
| JUnit Jupiter | 6.0.3 |
| Lombok | 1.18.46 |
| PostgreSQL driver | 42.7.13 |

Java 17 remains valid for Framework 7 and the Hibernate 7.2 artifacts. Hibernate's
Java 25 requirement for building Hibernate itself is not this application's JDK
requirement. Keep MapStruct 1.4.2.Final initially; processor compatibility remains
an execution gate, not a reason to upgrade unrelated libraries preemptively.

## Exact proposed change surface

1. POM: set Boot parent and spring-boot.version to 4.0.8; Springdoc to 3.0.3.
   Replace starter-web with starter-webmvc and starter-aop with starter-aspectj.
   Add test-scoped starter-webmvc-test and starter-data-jpa-test; retain test
   inclusion/exclusion rules and PostgreSQL opt-in safeguards. Use Boot-managed
   plugin versions. Do not change the wrapper, JDK, database or business logic.
2. Replace all three hibernate-jpamodelgen coordinates (main dependency, compiler
   processor path, IDE profile) with hibernate-processor. Inspect generated
   metamodels and MapStruct implementations; no generated-source hand edits.
3. Jackson: switch explicit Hibernate module to
   tools.jackson.datatype:jackson-datatype-hibernate7; switch HPPC and Jakarta
   XML-bind modules to tools.jackson coordinates, managed by the Jackson BOM.
   Remove the separate Java-time dependency and JavaTimeModule/Jdk8Module beans,
   whose capabilities are integrated in Jackson 3. Retain the Hibernate module
   with its Jackson 3 type. Keep com.fasterxml.jackson.annotation imports.
4. Adapt the two standalone HTTP mapper setups to the Jackson 3 builder/converter
   APIs. Preserve actual JSON assertions and timestamp/number/null behavior.
   Check module auto-discovery explicitly; do not silently opt into different
   defaults or introduce a second application mapper. A compatibility setting
   is allowed only when its effect is measured and documented.
5. Update moved Boot MVC/JPA test and embedded-Tomcat imports using the exact
   4.0.8 JAR APIs. MockitoBean stays unchanged. Preserve all test methods and
   meaningful assertions. Four legacy org.junit.Assert imports may be adapted
   to Jupiter only with overload/expected-actual semantics checked.
6. GroceryApp explicitly registers unused LiquibaseProperties although no
   migration engine/changelog exists. Proposed: remove that inert registration
   and its imports, rather than install/activate Liquibase. This is included
   explicitly in the approval scope; do not introduce schema migration.
7. Review ExceptionTranslator against Framework 7 headers/handler APIs.
   Preserve 400 fields and parser prefix, validation URI/messages, Allow header,
   empty entity 404s and approved 500 redaction. No changed snapshots to hide
   framework differences. Adjust implementation only to preserve the contract.

The starter/package/Jackson directions are supported by the Boot migration
guide; exact application changes above come from the current POM/source inventory.
Use small reviewed manual edits; no broad recipe or unrelated cleanup is proposed.

## Risk assessment and stop conditions

- Hibernate 7 introduces stricter mapping validation and changes around
  @MapsId/cascades. PurchaseProduct/SaleProduct, shared sequence generators,
  enum storage, timestamps, stock quantities and rollback are critical checks.
  Do not use ddl-auto=update to make a failure disappear.
- Jackson 3 builder/default changes can affect omitted fields, numeric values,
  dates, lazy entities and error detail formatting. Existing tests are a minimum,
  not exhaustive consumer coverage.
- Framework 7 changes HttpHeaders and test infrastructure. Exact advice API
  signatures must be checked against the downloaded candidate JARs.
- Spring Data JPA 4.0.7 is BOM-confirmed, but attempted version-specific migration
  documentation URLs were unavailable. Before production edits, inspect the
  maintained JPA release notes/source and candidate repository APIs for this
  project's derived queries/native report. Stop if that review finds a material
  unresolved change. Do not present BOM alignment as proof of query equivalence.
- No CI, coverage measurement, security assessment or production deployment is
  included. Existing credentials/configuration debt and workflow gaps remain.
- If dependencies cannot resolve, generated code breaks, schema validation fails,
  or preserving contracts requires broader changes: stop and document evidence.
  A Jackson 2 bridge or another version pair requires a revised proposal.

## Entry, verification and rollback

After explicit approval, use a new codex feature branch from clean local main.
Keep IDE automatic compilation from writing into Maven target during verification.
Re-read the current profile/baseline and rerun the unchanged entry baseline.
Review the candidate JAR APIs and unresolved documentation item above first.

Run focused MVC/context/mapping tests, then both complete commands:

```sh
./mvnw -B -ntp clean verify
./mvnw -B -ntp -Pdev,postgres-tests clean verify
```

Use private GROCERY_TEST_DB_* variables and a fresh schema-only grocery40_test
database created from grocery_boot27, with validation and existing _test guards.
Require all 55 existing cases (39 default plus 16 PostgreSQL); no disabled tests.
Verify stock signs, replacement/deletion, SQLSTATE 23503 and rollback.

Package and run on loopback with a separate schema-only grocery40_smoke database.
Compare with the Boot 3.5 stage: list/report outputs, missing IDs, all protected
400/500 contracts, timestamps, Swagger UI and full OpenAPI path/schema definitions.
Compare columns/defaults/nullability, constraints and sequence metadata.
Inspect resolved dependencies and JAR contents for unexpected duplicate JSON
stacks, missing drivers, processor/runtime leakage and correct Start-Class.

Record every diagnostic separately from regressions, measured counts and gaps.
Merge locally only after all gates pass; no push. Rollback is a reviewed revert
of the isolated stage; retain all prior databases and rerun the previous checks.

## Sources checked 2026-09-04

- [Boot 4.0.8 BOM](https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-dependencies/4.0.8/spring-boot-dependencies-4.0.8.pom)
- [Boot version metadata](https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-dependencies/maven-metadata.xml)
- [Springdoc matrix](https://springdoc.org/faq.html)
- [Springdoc 3.0.3 POM](https://repo.maven.apache.org/maven2/org/springdoc/springdoc-openapi/3.0.3/springdoc-openapi-3.0.3.pom)
- [Boot migration guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Framework 7 release notes](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-7.0-Release-Notes)
- [Hibernate 7.0](https://docs.hibernate.org/orm/7.0/migration-guide/),
  [7.1](https://docs.hibernate.org/orm/7.1/migration-guide/),
  [7.2 migration notes](https://docs.hibernate.org/orm/7.2/migration-guide/)
- [Jackson 3 release notes](https://github.com/FasterXML/jackson/wiki/Jackson-Release-3.0)
- [Hibernate Jackson module](https://github.com/FasterXML/jackson-datatype-hibernate)
- [Jackson 3.1.5 BOM](https://repo.maven.apache.org/maven2/tools/jackson/jackson-bom/3.1.5/jackson-bom-3.1.5.pom)
