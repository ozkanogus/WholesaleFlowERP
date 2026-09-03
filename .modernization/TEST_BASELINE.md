# Test Baseline

## Baseline context

| Field | Value |
| --- | --- |
| Revision | `d4efa460aed4f697b9333d42898b4e388a23cfb3` |
| Date | 2026-09-04 |
| Operating system | macOS 26.5.1, arm64 |
| Declared Java | 17 |
| Available Java | Temurin 17.0.20.1 |
| Maven wrapper | Wrapper 3.3.4, Maven 3.9.16 |
| Build entry point | `./mvnw clean verify` |
| Result | Production compile passes; test compile fails |

## Commands and observed result

The original checkout was blocked because no JDK was installed and the Maven
wrapper metadata was absent. Temurin 17.0.20.1 and Maven 3.9.16 were installed
under `~/.local`; wrapper 3.3.4 metadata and executable launcher scripts were
then restored.

The untouched project was first run with local Maven, then with the repaired
wrapper. Both runs compiled all 53 production sources and failed while compiling
the four test classes with the same 10 errors. Each error is an unresolved
Lombok-generated `builder()` method on `Grocery`, `Product`, `GroceryDTO`, or
`ProductDTO`.

The annotated classes do declare `@Builder`. The POM configures an explicit
`annotationProcessorPaths` list but omits Lombok, so Lombok processing is the
leading cause. This diagnosis must be verified by a minimal POM repair.

Maven also reports a pre-existing model warning: the
`properties-maven-plugin` declaration has no version.

## Existing automated tests

Static inspection found four service test classes and nine test methods:

| Area | Tests | Style |
| --- | ---: | --- |
| Grocery service | 2 | Mockito unit tests |
| Product service | 1 | Mockito unit test |
| Purchase service | 3 | Mockito unit tests |
| Sale service | 3 | Mockito unit tests |

The tests use JUnit Jupiter test annotations and Mockito's Jupiter extension,
with assertions imported from JUnit 4. This mixed style should be normalized
only after the current suite can be executed.

## Coverage gaps

No automated coverage was found for:

- REST status codes, payloads, validation, or exception translation
- repository queries and JPA mappings
- application-context startup and configuration binding
- PostgreSQL compatibility or Testcontainers execution
- stock movement update/delete edge cases
- transaction and rollback behavior
- schema creation or migration
- security expectations
- packaged application startup

No coverage measurement or CI workflow was found.

## Exit criteria for baseline repair

- The Java 17 and wrapper prerequisites above remain reproducible.
- `./mvnw clean verify` completes and its test totals and failures are recorded.
- Any pre-existing application/test failures are separated from bootstrap
  failures and from regressions introduced by the repair.
- No framework or application behavior changes are bundled into the repair.
