# Stage 4c — native error handler verified

2026-09-04. Approved implementation on codex/native-error-handler.
Boot 3.5.16, Springdoc 2.8.17 and Java 17 remain unchanged.

## Changes and intentional exception

Replaced Zalando ProblemHandling with Spring ResponseEntityExceptionHandler and
an application-owned ErrorResponse record. Business exceptions now extend
RuntimeException. Removed the two Zalando dependencies and their Jackson module
beans; Hibernate, Java time and JDK8 modules remain.

The owner explicitly approved suppressing unexpected-500 detail after a
synthetic baseline test demonstrated message disclosure. These responses now
contain title/status only. The new fallback logs exception type without arbitrary
exception text; this is not a general audit of existing application logging.
Existing tested business/parser/validation 400 contracts and empty entity 404s
are unchanged. The constraint-violation URI remains a wire identifier.

## Verification

Working directory: /Users/ozkanogus/Projects/SpringBootSampleERP.
Temurin 17.0.20.1, Maven 3.9.16, PostgreSQL 18.6.

| Check | Result |
| --- | --- |
| Unchanged baseline | 52 tests pass |
| ./mvnw -B -ntp -Pdev,postgres-tests clean verify | 55 pass: 38 Surefire + 17 Failsafe; 10.051 s |
| ./mvnw -B -ntp clean verify | 39 pass: 38 Surefire + 1 Failsafe; 6.991 s |
| Failures/errors/skips in final builds | 0/0/0 |
| Dependency tree and executable JAR | No Zalando artifacts; no Zalando imports |
| OpenAPI comparison | All 11 path definitions and component schemas exactly equal after JSON-key sorting |
| Packaged runtime | Loopback port 18082, grocery4c_smoke, ddl-auto=validate; pass |
| Database metadata | Columns/types/defaults/nullability and sequence increments match grocery_boot27 |

New advice tests cover 405 plus Allow, 415, invalid path ID, unexpected-500
redaction, and inherited multipart 400, unsupported-operation 501 and socket
timeout 504. Each exercises application/json and application/problem+json Accept
headers. Existing assertions were preserved; no tests were disabled.

Packaged checks verified lists (10 groceries, 10 products, 10 purchases, 30 sales,
400 stock movements), five empty missing-entity 404s, three report rows,
Swagger UI, three business 400s, malformed JSON and five validation violations.
The process was stopped afterwards. Baseline and smoke databases are retained.
An initial smoke script used stock-movements instead of the actual stockMovements
route; correcting the diagnostic URL resolved that 404 without code changes.

## Inherited advice review

The installed 0.28.0 interfaces/bytecode were inspected before removal:

- HTTP: method/media errors use Spring's classifications and preserve headers.
- Routing: missing parameter/part and servlet binding errors remain 400;
  no-handler mapping remains 404. Application missing-entity 404s bypass advice.
- IO: unreadable messages/type mismatch remain 400; multipart has an explicit
  400 handler. Compatibility overrides retain 400 for missing-path-variable,
  conversion-not-supported and max-upload exceptions instead of Spring defaults.
- Network: NetworkAdviceTrait inherits socket timeout only (504), now explicit.
  CircuitBreakerOpenAdviceTrait is not inherited; no circuit-breaker path exists.
- Validation: field/global binding and constraint-violation exceptions have
  explicit compatibility handling; localized validator messages are retained.
- General: the application's sole Problem subclass was BadRequestAlertException.
  UnsupportedOperation retains 501; Spring handles ResponseStatusException.
  No application ResponseStatus annotations/exceptions were found. The fallback
  catches Exception, not Throwable, as planned.

Synthetic/full-context tests do not exercise every unused inherited branch,
arbitrary Accept types, disconnected/committed responses or future async routes.
No application IO/network workflow was found beyond framework request handling.
This is not a claim of universal Zalando API equivalence or a security audit.

## Build interference and remaining gates

Two original-directory builds initially failed with generated mapper bytecode
containing unresolved-compilation stubs. Identical sources passed both builds in
an isolated directory. After the owner closed IDEs, both canonical builds passed
without mapper changes. This supports external compiler interference; the exact
process was not identified. Keep IDE automatic builds from sharing Maven output.

Logs under /private/tmp: grocery4c-final-verify.log,
grocery4c-final-default.log, grocery4c-final-smoke.log,
grocery4c-dependencies.log and grocery4c-openapi-before/after.json.
Earlier diagnostics are described in ERROR_HANDLER_STATUS.md.

No schema migration, CI, coverage measurement, deployment or push. Existing
Maven warnings and broader workflow gaps remain. Stage 4d requires a fresh
compatibility proposal and approval; Boot 4 was not started. Rollback is a
reviewed revert of this isolated stage followed by the prior verification.
