# Test Baseline

## Baseline context

Configuration hardening: 44 default / 60 PostgreSQL-profile tests pass.
Three conditional-demo tests and one packaged-configuration guard were added.
Existing enforced DataPopulator mock tests explicitly enable the conditional bean.
See CONFIGURATION_HARDENING_RESULT.md; older totals below are checkpoint history.

Latest: Java 21 / Boot 4.0.8 passes 56 full-profile and 40 default tests with
Modernizer 2.7.0, no failures/errors/skips. See JAVA21_RESULT.md.
Stage 4d on Java 17 previously passed the same totals.
A new context test verifies MVC uses Jackson 3 with Hibernate 7 module registration
and no Jackson 2 application mapper bean. See BOOT40_RESULT.md.
Stage 4c on Boot 3.5.16 previously passed 55 full-profile and 39 default tests.
Three advice test methods cover routing, inherited status mappings and approved
unexpected-500 redaction with both JSON Accept variants. See ERROR_HANDLER_RESULT.md.
Stage 4b previously passed 52 full-profile and 36 default tests.
WebServerTest starts a loopback random-port server, asserts Tomcat factory and
server types, and checks a real HTTP 404 response. See `TOMCAT_RESULT.md`.
Stage 4a error contracts remain in `BOOT4_TEST_PREPARATION_RESULT.md`.

Stage 3a repeat on Boot 3.1.12: all 46 opt-in tests and 30 default tests pass.
Jakarta imports, the Hibernate module factory reference and test logging syntax
were adapted; no assertions changed. See `STAGE3_RESULT.md` for initial failures
and packaged PostgreSQL smoke evidence.

| Field | Value |
| --- | --- |
| Original revision | `d4efa460aed4f697b9333d42898b4e388a23cfb3` |
| Date | 2026-09-04 |
| Operating system | macOS 26.5.1, arm64 |
| Declared Java | 21 |
| Available Java | Temurin 21.0.12.1; Java 17 retained for rollback |
| Maven wrapper | Wrapper 3.3.4, Maven 3.9.16 |
| Build entry point | `./mvnw clean verify` |
| Current result | Default: 44 pass; PostgreSQL profile: 60 pass (42 Surefire + 18 Failsafe) |

## Commands and observed result

The original checkout was blocked because no JDK was installed and the Maven
wrapper metadata was absent. Temurin 17.0.20.1 and Maven 3.9.16 were installed
under `~/.local`; wrapper 3.3.4 metadata and executable launcher scripts were
then restored.

The untouched project was first run with local Maven, then with the repaired
wrapper. Both runs compiled all 53 production sources and initially failed while
compiling the four test classes with the same 10 unresolved Lombok-generated
`builder()` references.

The annotated classes do declare `@Builder`. The POM configures an explicit
`annotationProcessorPaths` list but omitted Lombok. Adding the existing Lombok
dependency to that processor path resolved all 10 errors without changing
application or test behavior.

The final command completed successfully:

```text
$ ./mvnw clean verify
Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

The build produced `target/grocery-0.0.1-SNAPSHOT.jar` (approximately 56 MB).

Maven also reports a pre-existing model warning: the
`properties-maven-plugin` declaration has no version.

## Existing automated tests

The original four classes contained nine tests. The first characterization slice
added eight tests in two classes. MVC, persistence, and context tests bring the
total to twenty-four. Five lookup regressions and one packaging regression brought
the default total to thirty. Stage 4a added five error cases; Stage 4b adds
one real-server case (36 default):

| Area | Tests | Style |
| --- | ---: | --- |
| Grocery service | 2 | Mockito unit tests |
| Product service | 1 | Mockito unit test |
| Purchase service | 3 | Mockito unit tests |
| Sale service | 3 | Mockito unit tests |
| Stock movement service | 3 | Mockito characterization tests |
| Grocery resource | 5 | Direct controller-method characterization tests |
| Grocery HTTP contracts | 4 | Standalone MockMvc with application Jackson modules |
| Error advice contracts | 3 | Standalone MockMvc, routing/status compatibility and 500 redaction |
| Persistence mappings | 2 | DataJpaTest with isolated H2 and rollback |
| Application context, mapper and error contracts | 7 | SpringBootTest/MockMvc with isolated H2 and MockitoBean DataPopulator |
| Embedded server | 1 | Real loopback Tomcat with isolated H2, factory/type and HTTP assertions |
| Resource lookups | 5 | MockMvc checks existing and missing records across all resources |
| Packaged entry point | 1 | Failsafe checks manifest and class entries after packaging |

All tests use JUnit Jupiter annotations and Mockito's Jupiter extension. The
original JUnit 4 object-equality imports were adapted to Jupiter in Stage 4d;
all tests now use Jupiter assertions.

The new tests cover positive purchase quantities, negative sale quantities,
replacement-call ordering, the current no-op for INVENTORY, and grocery resource
responses. They document current behavior, not approval of every business rule.
Missing records originally returned HTTP 200 with a null body in all five
resources, despite controller documentation promising 404. Five regression
tests reproduced the failure before a separate behavior fix. All five lookup
endpoints now return 404 with an empty body for missing records and retain 200
with the DTO for existing records. No dependency upgrade was included.

The packaged JAR originally named `groceryApp` as its Start-Class, but the class
is `GroceryApp`. A Failsafe test reproduced the mismatch before correcting the
POM. It now verifies the manifest's application and launcher entries exist in
the JAR. This is an artifact check, not a process-startup or database test.

## Coverage gaps

Stage 2 final slice adds six invocations: two service-owned foreign-key failure
rollback cases and four full-context purchase/sale MockMvc cases. All 46 tests
pass without skips. DTO IDs, quantity, price, timestamp and selected 400/404
contracts are protected. PostgreSQL SQLSTATE 23503 is asserted before checking
absence of partial aggregate/line/stock writes. See `POSTGRES_TESTS.md` for limits.

The next Stage 2 slice adds six real-service PostgreSQL workflow invocations
(three parameterized methods, purchase and sale each). The 40-test full build
passes without skips. It covers persisted line prices/quantities, signed stock,
product replacement, deletion cascades, DTO reload and rollback of flushed writes
within a surrounding transaction. See `POSTGRES_TESTS.md` for exact limitations.

Stage 2 adds four opt-in PostgreSQL report tests; all 34 tests pass without skips.
Missing database settings were separately verified to fail. Native quantity
ranking, month edges, grocery isolation, empty results and approved product-ID
tie ordering are covered. See `POSTGRES_TESTS.md` for setup and scope.

Stage 1 repeated `./mvnw -B -ntp clean verify` on Boot 2.7.18 with the same
Java 17 toolchain: 29 Surefire and 1 Failsafe tests passed, zero failures,
errors or skips (17.329 seconds). No tests or assertions changed. The packaged
application also passed fresh PostgreSQL smoke checks; see `MIGRATION_REPORT.md`.

Manual PostgreSQL startup and API checks passed; see `POSTGRES_SMOKE_TEST.md`.
They do not replace the automated coverage gaps below.

No automated coverage was found for:

- HTTP contracts outside existing grocery cases and full-context purchase/sale
  GET, malformed JSON and ID-error cases (successful HTTP writes remain a gap)
- multi-line purchase/sale aggregates and same-product quantity edits
- default runtime configuration and demo-data startup (the context smoke test
  uses H2 create-drop and replaces the random DataPopulator with a mock)
- Testcontainers execution
- stock movement update/delete edge cases
- rollback on failures other than the covered foreign-key/forced-failure cases
- schema creation or migration
- security expectations
- packaged application startup

No coverage measurement or CI workflow was found.

## Baseline repair result

- Java 17 and Maven wrapper prerequisites are reproducible.
- `./mvnw -version` reports Maven 3.9.16 and Temurin 17.0.20.1.
- `./mvnw clean verify` succeeds with 30 passing tests, including the packaged-artifact check.
- The original bootstrap and test-compilation failures are recorded separately.
- Baseline repair changed no framework version or application behavior; the later
  missing-record response correction is documented separately above.
