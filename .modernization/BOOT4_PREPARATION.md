# Boot 4 preparation — decision gate

Reviewed 2026-09-04. Stage 4a was subsequently approved and completed; see
`BOOT4_TEST_PREPARATION_RESULT.md`. Stage 4b is also complete; see
`TOMCAT_RESULT.md`. Stages 4c–4d remain unapproved.
Current baseline: Boot 3.5.16, Springdoc 2.8.17, Java 17, Maven 3.9.16.
All 46 PostgreSQL-profile tests and 30 default tests passed at the preceding
checkpoint; see `BOOT35_RESULT.md`. No new build was needed for this docs-only
review. No production dependency, code, test, schema or runtime setting changed.

## Findings and decisions

| Area | Evidence / confidence | Proposed direction |
| --- | --- | --- |
| Java | Confirmed: Boot 4 permits Java 17 | Keep the installed JDK; no simultaneous runtime upgrade |
| Server | Confirmed: Boot 4 removes Undertow; this POM resolves both Tomcat and Undertow | Establish the active factory, then standardize on Tomcat in an isolated Boot 3.5 change |
| Test mocks | Confirmed: Boot 4 removes MockBean; two fields use it | Replace with Framework MockitoBean on Boot 3.5 |
| Error library | Confirmed: Zalando README documents Boot 3/Spring 6 support; Boot 4 support is unconfirmed | Prefer replacing the adapter with native Spring ProblemDetail after characterizing contracts |
| JSON | Confirmed: Boot 4 defaults to Jackson 3; current JacksonConfiguration uses Jackson 2 modules | Migrate modules with the framework; do not assume identical JSON output |
| Starters/tests | Confirmed: Boot 4 modularizes packages and starters | Map MVC/JPA tests explicitly; migrate AOP starter to AspectJ starter |
| ORM processor | Confirmed: hibernate-jpamodelgen management moves to hibernate-processor | Update all three POM occurrences and inspect generated metamodels |
| OpenAPI | Confirmed: Springdoc matrix pairs Boot 4.0 with 3.0.x | Select and verify an exact patch pair at the execution gate |

Sources: [Boot migration guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide),
[Zalando support statement](https://github.com/zalando/problem-spring-web#dependencies),
[Spring error responses](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html),
[Springdoc matrix](https://springdoc.org/#what-is-the-compatibility-matrix-of-springdoc-openapi-with-spring-boot).
Accessed 2026-09-04.

## Proposed execution graph

Each row requires separate approval, a focused codex branch and a local merge
only after verification. No push or deployment is included.

1. **4a — test preparation on Boot 3.5.16.** Replace the two MockBean fields in
   GroceryContextTest and PostgresWorkflowIT, preserving demo-data isolation.
   Add full-context error contracts for supplied-ID create, missing-ID update,
   malformed JSON and validation: status, media type, stable title/detail/type
   and violation fields where present. Preserve existing assertions and observe
   baseline output before adding expectations; do not normalize differences away.
   No production changes or new library versions. Run default and PostgreSQL
   clean verify; record the new case count and require all original 46 cases.

2. **4b — server clarification.** First identify the actual web-server factory
   using a runtime assertion. Remove the redundant Undertow starter and retain
   Tomcat. If the active server changes, record this explicitly. Run both builds
   and packaged HTTP/OpenAPI smoke. Do not claim a server change solely from the
   dependency list; previous smoke logs did not establish the factory type.

3. **4c — error adapter replacement on Boot 3.5.** After 4a, propose the exact
   native Spring advice implementation replacing ExceptionTranslator,
   BadRequestAlertException and Zalando modules/dependencies. Retain documented
   contracts and empty missing-resource 404 bodies. Any response-shape change
   needs owner approval, not snapshot replacement. Full suite plus smoke.

4. **4d — framework transition.** Only after prerequisites are green, lock a
   Boot 4 patch and compatible Springdoc patch, review their managed dependency
   BOMs, Framework/Data/Hibernate migration notes and processor/Jackson modules.
   Then propose an exact execution patch for approval. Verify existing/new tests,
   generated code, schema validation, packaged startup, API and OpenAPI schemas.
   Use fresh schema-only PostgreSQL databases; never update a prior baseline.

## Target and unresolved work

Stages 4a–4b are complete; the next gate is the error adapter (4c), not Boot 4. Boot 4.0
is the narrower framework transition candidate; Boot 4.1 is an alternative to
evaluate for the eventual deployment target and support window. Exact patches
are intentionally not locked while error/server decisions are unresolved.
Remaining unknowns include Hibernate major-version mapping behavior, MapStruct
1.4.2 with the new processor stack, lazy-entity serialization, and complete
OpenAPI/error-schema equivalence. The current tests do not prove these.

A temporary Jackson 2 bridge is an alternative, but it does not establish
Zalando's Spring 7 compatibility and leaves another migration pending. Prefer
native error handling plus Jackson 3 unless concrete evidence changes the plan.

For each step, rollback is a reviewed revert of its isolated commit and a repeat
of the previous green checks. Retain earlier databases and never delete business
data. Stop on unexplained failures, schema changes or expanded behavior scope.
Boot 3.5 remains the local development baseline, not deployment approval.
