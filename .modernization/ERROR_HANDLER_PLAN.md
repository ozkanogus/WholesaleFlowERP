# Stage 4c — Spring-native error handling proposal

Prepared 2026-09-04. Execution approved; unexpected-500 redaction also approved.
Implementation verified: ERROR_HANDLER_RESULT.md. Baseline: Boot 3.5.16,
Java 17, Tomcat; 52 full-profile and 36 default cases pass. This document-only
proposal originally did not replace dependencies or claim new test results.

## Decision

Replace Zalando's adapter with Spring MVC exception handling and a small
application-owned compatibility response model. Do not enable Boot's default
problem-details advice globally or return an uncustomized ProblemDetail body.
The latter can supply default fields, including an instance path, which are
absent from current responses. Spring supplies exception classification and
ResponseEntityExceptionHandler; the application preserves its wire format.

This is preferable to retaining a Jackson 2 compatibility module solely for
Zalando: that does not establish Spring 7 compatibility. Native ProblemDetail
with its default wire shape is a viable future API revision, not this migration.

Evidence: [Spring error response documentation](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html),
accessed 2026-09-04; local ExceptionTranslator, BadRequestAlertException,
JacksonConfiguration, GroceryContextTest and GroceryHttpTest. Inspection of
the installed ProblemHandling interface confirms it also inherits general,
HTTP, IO, network, routing and validation advice, beyond the five new tests.

## Protected contracts

| Case | Response to preserve |
| --- | --- |
| Valid grocery POST with ID | 400; title `A new grocery cannot already have an ID`, numeric status; omit type/detail/violations |
| Valid grocery PUT without ID | 400; title `Invalid id`; same omissions |
| Valid grocery PUT with unknown ID | 400; title `Entity not found`; same omissions |
| Malformed JSON | 400; `Bad Request`, stable parser-detail prefix; omit type/violations |
| Stock-movement validation | 400; existing constraint-violation URI, title, five field/message pairs; omit detail |
| Missing entity GET | Empty 404, not a synthesized problem body |

Media type remains application/problem+json for tested error bodies. Do not add
instance, stack traces, null-valued fields or exception class names. Validation
messages retain request locale; sorting by field makes output deterministic.
No parser source-location formatting or violation-order guarantee is added.

## Exact implementation scope

1. Re-run the unchanged baseline. Before replacing advice, add characterization
   for 405 (including Allow), 415, invalid path-variable type, and a controlled
   unexpected exception via a test-only controller. Capture media negotiation
   with JSON and problem+json Accept headers. Inspect remaining inherited advice
   and classify applicability; do not assume five green tests prove equivalence.
2. Change BadRequestAlertException to an application RuntimeException retaining
   its public constructor and message. No controller/service edits are needed.
3. Rewrite ExceptionTranslator using ResponseEntityExceptionHandler and explicit
   handlers/overrides for the observed cases. Add a small response DTO with
   optional fields omitted, plus field/message violation values. Preserve
   status-specific headers and avoid duplicate handlers. Retain safe server-side
   logging without broad Throwable catches. If the baseline exposes sensitive
   unexpected-exception details, stop and request a separate redaction decision.
4. Remove problem-spring-web and jackson-datatype-problem dependencies and the
   problem-spring-web.version property from pom.xml. Remove only ProblemModule
   and ConstraintViolationProblemModule beans/imports from JacksonConfiguration.
   Keep all unrelated Jackson/Hibernate/time modules and library versions.
5. Adapt GroceryHttpTest's mapper setup to remove the two deleted module calls;
   preserve all existing assertions. Add focused tests for the new response
   model only where the HTTP contracts do not already exercise it.

If broader advice behavior cannot be preserved within this scope, stop with the
specific discrepancy rather than silently falling through to Boot's /error.
Unmapped IO/network cases are an explicit residual review item, not implicitly
accepted behavior changes. No framework/JDK/Jackson major upgrade is authorized.

## Verification and rollback

Use the project directory and installed Java 17. Run focused contract tests,
then `./mvnw -B -ntp -Pdev,postgres-tests clean verify` against grocery35_test
with ddl-auto=validate, and `./mvnw -B -ntp clean verify`. All existing 52 cases
must remain green plus the new cases; no assertion weakening. Inspect dependency
tree and packaged JAR for remaining Zalando artifacts/imports.

Packaged smoke uses a new schema-only database, loopback binding and private
credentials. Verify CRUD reads, report results, error contracts, OpenAPI response
schemas, Swagger UI and unchanged database metadata. Compare error schemas before
and after, not only the OpenAPI path count. No data/schema migration or push.

Use one focused feature branch; commit and merge locally only on green. Rollback
is a reviewed revert of this isolated stage and repeat of the prior baseline.
Retain prior databases. Stop before Boot 4 execution or any approved-contract change.
