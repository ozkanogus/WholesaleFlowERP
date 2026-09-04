# Stage 4a — test preparation

Owner approved 2026-09-04; GREEN. Boot 3.5.16, Springdoc 2.8.17 and Java 17
remain unchanged. Branch: `codex/boot4-test-preparation`. No production edits,
dependency changes, database schema changes, server replacement or remote push.

## Changes

Replaced both MockBean fields with Framework MockitoBean(enforceOverride=true).
Required override fails if the real DataPopulator bean disappears, instead of
silently creating a replacement. The H2 context test additionally verifies the
injected mock identity and absence of demonstration grocery rows. Existing
service/resource assertions and all PostgreSQL workflow assertions remain.

Added five full-context MockMvc cases within GroceryContextTest:

- Otherwise-valid grocery create with supplied ID: custom title and status 400.
- Otherwise-valid grocery update without ID: Invalid id title and status 400.
- Otherwise-valid grocery update of nonexistent -1: Entity not found title;
  fixture verifies that ID is absent.
- Malformed JSON: Bad Request title, status and stable parser-detail prefix.
- Empty stock movement: constraint-violation type/title, all five field names
  and English messages, independent of violation ordering.

All assert application/problem+json and numeric status. Custom errors assert
the observed absence of type/detail/violations; validation asserts absence of
detail. Grocery rejection cases assert no row was created. Request language is
fixed to English. No mocked controller, service, repository or error translator
is used; H2 supplies the full persistence context and only demo startup is mocked.

## Evidence

Existing PostgreSQL baseline passed 46 cases in 10.466 seconds. Temporary
observation code ran before assertions were finalized and was removed. It
showed that an ID-only grocery payload hits name validation rather than the
controller guard; the final cases include a valid name to reach each ID guard.

`./mvnw -B -ntp -Dtest=GroceryContextTest test`: six cases passed.
`./mvnw -B -ntp -Pdev,postgres-tests clean verify`: 51 cases passed
(34 Surefire + 17 Failsafe), zero failures/errors/skips, 10.722 seconds.
`./mvnw -B -ntp clean verify`: 35 cases passed, zero failures/errors/skips.
Environment: Temurin 17.0.20.1, Maven 3.9.16, macOS arm64, PostgreSQL 18.6;
working directory `/Users/ozkanogus/Projects/SpringBootSampleERP`.
PostgreSQL uses the existing dedicated grocery35_test with ddl-auto=validate.
Local logs: `/private/tmp/grocery4a-{baseline,observe,observe-valid,focused,verify,default}.log`.

## Limits and next gate

Parser source-location formatting and validation-array order are deliberately
not fixed contracts. Complete error coverage across every controller, nested
validation, concurrency and unexpected-server-error behavior remain gaps.
No production mutation test or new packaged smoke was run for this test-only
change; the unchanged production artifact's prior smoke is in BOOT35_RESULT.md.
No coverage percentage is claimed. Existing unrelated warnings remain.

Next proposed step is 4b: establish the active server factory, then remove
Undertow and verify Tomcat explicitly. Separate approval is required. Error
adapter replacement and Boot 4 execution remain unapproved.
