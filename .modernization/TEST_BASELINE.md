# Test Baseline

## Baseline context

| Field | Value |
| --- | --- |
| Original revision | `d4efa460aed4f697b9333d42898b4e388a23cfb3` |
| Date | 2026-09-04 |
| Operating system | macOS 26.5.1, arm64 |
| Declared Java | 17 |
| Available Java | Temurin 17.0.20.1 |
| Maven wrapper | Wrapper 3.3.4, Maven 3.9.16 |
| Build entry point | `./mvnw clean verify` |
| Current result | Build success; 17 tests pass |

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
adds eight tests in two classes, for seventeen tests in total:

| Area | Tests | Style |
| --- | ---: | --- |
| Grocery service | 2 | Mockito unit tests |
| Product service | 1 | Mockito unit test |
| Purchase service | 3 | Mockito unit tests |
| Sale service | 3 | Mockito unit tests |
| Stock movement service | 3 | Mockito characterization tests |
| Grocery resource | 5 | Direct controller-method characterization tests |

All tests use JUnit Jupiter annotations and Mockito's Jupiter extension. The
original tests import JUnit 4 assertions; the new tests use Jupiter assertions.

The new tests cover positive purchase quantities, negative sale quantities,
replacement-call ordering, the current no-op for INVENTORY, and grocery resource
responses. They document current behavior, not approval of every business rule.
In particular, missing groceries currently return HTTP 200 with a null body,
despite the controller documentation promising 404. Correct this separately.

## Coverage gaps

No automated coverage was found for:

- HTTP routing, JSON payloads, validation, or exception translation (the new
  controller tests invoke Java methods directly, not the HTTP stack)
- repository queries and JPA mappings
- application-context startup and configuration binding
- PostgreSQL compatibility or Testcontainers execution
- stock movement update/delete edge cases
- transaction and rollback behavior
- schema creation or migration
- security expectations
- packaged application startup

No coverage measurement or CI workflow was found.

## Baseline repair result

- Java 17 and Maven wrapper prerequisites are reproducible.
- `./mvnw -version` reports Maven 3.9.16 and Temurin 17.0.20.1.
- `./mvnw clean verify` succeeds with 17 passing tests after characterization.
- The original bootstrap and test-compilation failures are recorded separately.
- No framework version or application behavior was changed.
