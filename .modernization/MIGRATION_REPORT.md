# Migration Report

Updated: 2026-09-04.

## Outcome

Boot 4 planning review is recorded in `BOOT4_PREPARATION.md`. No implementation
was performed; test preparation is the next proposed approval gate.

Latest: Stage 3e GREEN (Boot 3.5.16 / Springdoc 2.8.17), Java 17 unchanged.
All 46 tests and packaged PostgreSQL/API/OpenAPI checks pass. Only three POM
versions changed. See `BOOT35_RESULT.md`; no next stage or push is authorized.

Earlier checkpoint: Stage 3a GREEN (Boot 3.1.12/Jakarta), Java 17 unchanged.
All 46 tests and packaged PostgreSQL smoke checks pass. See `STAGE3_RESULT.md`
for exact dependency changes, compatibility fixes, commands and remaining risks.
Later minor-version stages are not approved. Earlier updates below are historical.

Latest update (2026-09-04): Stage 2 is GREEN / COMPLETE. The full PostgreSQL
build passes 46 tests (29 Surefire + 17 Failsafe), zero failures/errors/skips.
The preceding 40-test baseline was green. New tests cover real service-owned
foreign-key rollback and full-context purchase/sale DTO/error contracts. No
production changes were required. Exact scope and residual gaps are documented
in `POSTGRES_TESTS.md`; Stage 3 remains unapproved. Earlier slice updates below
are retained as historical evidence, not the current stage status.

Workflow-slice update (2026-09-04): 40 tests pass in the PostgreSQL profile
(29 Surefire + 11 Failsafe), including six new service workflow cases. The
preceding 34-test baseline was rerun successfully. Only tests and documentation
changed. See `POSTGRES_TESTS.md` for cascade/replacement/rollback evidence and
limits; Stage 2 remains IN PROGRESS. No new framework stage or remote push.

Stage 2 update: approved and IN PROGRESS. Reporting slice adds four PostgreSQL
tests (34 total in the opt-in full build, zero failures/errors/skips). The owner
approved product-ID ascending as the tie-break for equal current-month quantity
sums per grocery. This is an intentional behavior clarification, not a framework
change. Test infrastructure was committed separately from the query change.
The original 30-test build remains green; missing PostgreSQL settings correctly
fail the opt-in suite. See `POSTGRES_TESTS.md` for reproducible setup, fixture
diagnostics and remaining Stage 2 gates. No later framework stage was executed.

Stage 1 is GREEN / COMPLETE: Spring Boot 2.6.3 -> 2.7.18 with Java 17 and
Maven 3.9.16 unchanged. The overall modernization is PARTIAL. The owner approved
this stage on 2026-09-04. Later stages require separate approval.

Only the parent and explicit Boot version changed in `pom.xml`; no application
code, configuration defaults, tests or assertions changed. No compatibility
fixes or unmanaged dependency replacements were required. Work is recorded on
`codex/boot-2-7`; no remote publication is part of this stage.

## Verification

Working directory: `/Users/ozkanogus/Projects/SpringBootSampleERP`.
Environment: macOS arm64, Temurin 17.0.20.1, PostgreSQL 18.6.

```sh
export JAVA_HOME=/Users/ozkanogus/.local/opt/temurin-17
./mvnw -B -ntp clean verify
```

| Check | Before | After |
| --- | --- | --- |
| Full build | PASS | PASS; 17.329 seconds |
| Surefire | 29 passed | 29 passed |
| Failsafe packaged entry-point check | 1 passed | 1 passed |
| Failures / errors / skips | 0 / 0 / 0 | 0 / 0 / 0 |
| Packaged PostgreSQL startup | PASS | PASS; fresh `grocery_boot27` database |
| List/report smoke responses | PASS | Same HTTP status and record counts |
| Schema comparison | Baseline seven tables | Columns/types/defaults/nullability and constraints identical |
| Coverage measurement | Not measured | Not measured |
| CI / deployment | Not run | Not run |

For runtime reproduction, create a new empty database on the dedicated cluster,
set `SPRING_DATASOURCE_URL` to its JDBC URL at `127.0.0.1:55432`, set
`SPRING_DATASOURCE_USERNAME` and `SPRING_DATASOURCE_PASSWORD` from local private
configuration, and set `SERVER_ADDRESS=127.0.0.1` and `SERVER_PORT=18082`. Run:

```sh
"$JAVA_HOME/bin/java" -jar target/grocery-0.0.1-SNAPSHOT.jar
```

Repeat the endpoints in `POSTGRES_SMOKE_TEST.md`. This run also checked empty
404 responses for all five missing-resource endpoints and the numeric 400
Problem response for a grocery create request containing an ID. The latter
intentionally produces a logged error/warning; no startup errors were observed.
OpenAPI returned 11 paths. SQL confirmed 100 positive PURCHASE stock rows
(11..20) and 300 negative SALE rows (-10..-1). Random fixture values were not
compared as exact business expectations.

Schema comparison used ordered `information_schema.columns` rows and
`pg_get_constraintdef` for public-table constraints in the baseline and new
database. This does not validate schema migration of existing data or compare
every database object. A first stock-sign diagnostic used nonexistent column
`quantity`; inspecting the schema and querying actual column `count` corrected
the diagnostic, without changing application code or data.

The API process was stopped after checks. PostgreSQL and both isolated demo
databases remain. Local logs: `/private/tmp/grocery-boot27.log` and
`/private/tmp/grocery-boot27-startup.log` (not durable or committed artifacts).

## Dependency and warning review

Resolved packaged components include Spring Framework 5.3.31, Hibernate
5.6.15.Final, Jackson Databind 2.13.5, PostgreSQL JDBC 42.3.8, Tomcat 9.0.83
and Undertow 2.2.28.Final. Both server libraries remain packaged; their cleanup
is outside this stage. Existing Maven warnings remain: missing explicit
properties-maven-plugin version and read-only resources parameter configuration.
No warning-driven broad cleanup was performed.

## Deviations and remaining risks

The feature branch included the pending migration-plan commit from
`codex/migration-plan`; the planned documentation is merged with this verified
stage. No other execution scope deviation occurred.

No intentional business behavior changes occurred. Manual smoke tests do not
prove transaction rollback, purchase/sale cascades, deterministic report ranking
or month boundaries. Demo seeding, schema auto-update and local credential
defaults remain unchanged and unsuitable for a future deployment.

Next proposed step is Stage 2's deterministic PostgreSQL and contract safety net.
Boot 2.7.18 is only a migration bridge, not an approved deployment target.
README, profile, test baseline, smoke evidence and migration plan were updated;
the working agreement remains applicable unchanged.
