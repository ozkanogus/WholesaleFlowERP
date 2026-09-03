# Test Baseline

## Baseline context

| Field | Value |
| --- | --- |
| Revision | `d4efa460aed4f697b9333d42898b4e388a23cfb3` |
| Date | 2026-09-04 |
| Operating system | macOS 26.5.1, arm64 |
| Declared Java | 17 |
| Available Java | None |
| Build entry point attempted | `sh mvnw -version` |
| Result | Blocked before compilation |

## Commands and observed result

```text
$ java -version
Unable to locate a Java Runtime.

$ sh mvnw -version
Unable to locate a Java Runtime.
```

The wrapper is also structurally incomplete:

- `.mvn/wrapper/maven-wrapper.properties` is missing.
- `.mvn/wrapper/maven-wrapper.jar` is missing.
- `mvnw` is not marked executable.
- During the attempt, the script tried to obtain wrapper components but could
  not write the absent wrapper path.

No compilation or tests ran. This is an environment/repository bootstrap block,
not evidence that the application or tests fail.

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

- A supported Java 17 JDK is selected and recorded.
- A complete Maven wrapper is committed and executable.
- `./mvnw -version` succeeds without relying on a global Maven installation.
- `./mvnw clean verify` completes and its test totals and failures are recorded.
- Any pre-existing application/test failures are separated from bootstrap
  failures and from regressions introduced by the repair.
- No framework or application behavior changes are bundled into the repair.
