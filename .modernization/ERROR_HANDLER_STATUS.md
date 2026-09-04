# Stage 4c — verification complete

Final update: both canonical builds pass after IDEs were closed. Packaged API,
OpenAPI and schema checks pass. See ERROR_HANDLER_RESULT.md for final evidence.
The earlier checkpoint below is retained as diagnostic history, not current status.

2026-09-04. Work remains on `codex/native-error-handler`; no commit, merge or push.

## Approval and implementation

The owner approved replacing the adapter and subsequently approved omitting
internal details from unexpected 500 responses. The prior redaction decision
gate is resolved. The pre-change synthetic exception exposed its diagnostic
message for both JSON Accept variants; no real secret was used.

Implemented Spring ResponseEntityExceptionHandler, an application-owned response
record, RuntimeException-based business errors, removal of Zalando dependencies
and Jackson modules, and compatibility tests. The tested 400 responses remain
unchanged. Unexpected 500 responses contain only title/status; server logging
does not include arbitrary exception messages. This is not yet a completed stage.

## Evidence and current blocker

- Unchanged baseline: 52 tests pass (10.574 s).
- Before replacement: observed 405 with Allow=GET, 415 and invalid path ID 400,
  all application/problem+json with both JSON Accept variants.
- Focused migrated checks: 12 tests passed, including full-context 400 contracts.
- Two full clean builds in the project directory failed loading PurchaseMapperImpl:
  NoClassDefFoundError for unqualified GroceryMapper. The generated Java source
  is correctly packaged, but javap shows compiled methods containing
  "Unresolved compilation problems" stubs and unqualified types.
- Identical source snapshot, without target/.git, in
  /private/tmp/grocery4c-isolated.tlXCpa passes the PostgreSQL full build:
  55 tests (38 Surefire + 17 Failsafe), zero failures/errors/skips, 10.209 s.
  Three added test methods cover routing, redaction, and inherited 400/501/504
  mappings with both Accept variants. No test was disabled or weakened.
- The isolated default clean build also passes: 39 tests, zero failures/errors/
  skips, 6.918 s. Log: /private/tmp/grocery4c-isolated-default.log.
- This strongly suggests external compiler interference with the original target
  directory; the responsible process is not confirmed (process listing denied).
  Close/pause any IDE automatic Java build for this repository before repeating
  the canonical project-directory check. Do not change business mappers to work
  around corrupted build output.

## Remaining verification

Repeat both canonical builds in the original directory, finish inherited advice
review, compare packaged OpenAPI response schemas, run fresh PostgreSQL runtime
checks, inspect dependency tree/JAR, review the complete diff and update final
migration records. Merge locally only after those gates pass. Boot 4 is not started.

The baseline packaged application ran on grocery4c_before and its OpenAPI output
was saved to /private/tmp/grocery4c-openapi-before.json; the process was stopped.
grocery4c_smoke has been created from schema-only grocery_boot27 but has not yet
been used by the new application. Existing databases were retained.

Logs: /private/tmp/grocery4c-routing-before.log,
/private/tmp/grocery4c-focused.log, /private/tmp/grocery4c-verify.log,
/private/tmp/grocery4c-verify-repeat.log, /private/tmp/grocery4c-isolated-verify.log.
