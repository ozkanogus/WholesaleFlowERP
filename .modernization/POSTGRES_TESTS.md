# PostgreSQL integration tests

Stage 2 reporting and workflow slices, approved 2026-09-04. They were repeated
successfully on subsequent Boot checkpoints; the current database is below,
leaving `grocery_stage2_test` as the Boot 2.7 baseline database.

## Isolated setup

Current Boot 3.3 checkpoint uses `grocery33_test`, copied from the same baseline
schema. Earlier checkpoint databases remain untouched.

Use a dedicated database whose name ends in `_test`. Never use business data.
The local database `grocery_stage2_test` was created empty and populated with
schema-only output from the Stage 1 `grocery_boot27` database using PostgreSQL
`pg_dump --schema-only --no-owner --no-privileges` piped to `psql` with
`ON_ERROR_STOP=1`. No baseline data was copied or removed.

The test uses schema validation, not schema update or create-drop. For a new
environment, provision the schema from the verified Boot 2.7 baseline before
running it. Automated schema provisioning remains future work. Use a dedicated
database role where practical; the database-name guard is not a security boundary.

Set these environment variables (do not commit the password):

```sh
export GROCERY_TEST_DB_URL=jdbc:postgresql://127.0.0.1:55432/grocery33_test
export GROCERY_TEST_DB_USER=ozkanogus
# Set GROCERY_TEST_DB_PASSWORD from your private local credential store.
./mvnw -B -ntp -Pdev,postgres-tests clean verify
```

Keep `dev` explicitly enabled to preserve the baseline dependency profile.
The regular `./mvnw clean verify` excludes PostgreSQL tests intentionally and
still runs the original 30 tests. The opt-in command adds four report tests and
twelve workflow/HTTP invocations (46 total);
missing settings, an unavailable server, or an incompatible schema fail the run.

## Protected behavior

- Sum quantities across sales, independently of price, and return at most three.
- Restrict to the requested grocery and current PostgreSQL calendar month.
- Include the first and last microsecond of the month; exclude adjacent months.
- Return no products when the grocery has no sales.
- Order equal sums by product ID ascending before limiting and in the result.
  The owner explicitly approved this tie-break behavior on 2026-09-04.

Fixtures use fixed values and PostgreSQL transaction-stable `now()` expressions,
so month-boundary execution does not depend on the JVM clock. Session timezone
semantics remain unchanged; no business timezone policy has been introduced.
Each DataJpaTest transaction rolls back its inserted rows. Sequence advances
remain, as expected in PostgreSQL. The slice does not start the demo initializer.

Direct SQL report fixtures exercise the real repository query. The separate
`PostgresWorkflowIT` starts the Spring context with only DataPopulator mocked;
services, repositories and MapStruct mappers are real. Three parameterized tests
run for both purchases and sales and verify:

- Persisted line quantities/prices and correctly signed stock movements.
- Reloaded DTO product IDs and quantities after flushing/clearing JPA state.
- Replacing a product removes the old line and movement instead of appending.
- Deleting an aggregate removes its lines and stock, but retains product/grocery.
- A forced failure after flushed writes rolls back aggregate, lines and stock;
  absence is verified outside the failed transaction.

All workflow fixtures have fixed names, quantities and timestamps. Their explicit
transactions always roll back; no application defaults or schema are modified.
The original forced-failure test verifies participation in a surrounding
transaction. Two additional cases call the real service without an outer
transaction, use a nonexistent grocery reference, require PostgreSQL SQLSTATE
23503, and verify no aggregate, line or movement remains. Each commits one
prerequisite product and removes that exact row in a finally block. This covers
foreign-key failure, not every failure timing or invalid-input case.

Four full-context MockMvc cases cover purchase/sale DTO JSON (IDs, quantities,
prices and ISO timestamp), supplied-ID create rejection, missing-ID update
rejection, malformed JSON and empty 404 responses. The same application Jackson
and exception-handler configuration is used, with no mock controllers/services.
MockMvc runs synchronously in-process, not over a network socket; successful GET
fixtures remain within a rollback-only test transaction.

Multi-line aggregates, same-product quantity edits, nested validation semantics,
HTTP create/update success, and operation-ID collisions across types remain gaps.

## Evidence and limitations

The unchanged 30-test baseline passed before editing. The initial fixture used
the wrong sequence name; correcting it to the existing `sequence_generator`
made the first three PostgreSQL tests pass. This was a test setup defect, not
an application regression.

The new tie test also happened to pass against the original query. That result
does not guarantee ordering: the original SQL specified no tie-break. The query
now explicitly orders both the limited subquery and outer result. No changes to
quantity aggregation, month selection, joins or grocery filtering were made.

The preceding 34-test baseline and the expanded 40-test build both passed on
2026-09-04. No production changes or weakened assertions were needed. Stage 2
was then extended to 46 passing tests by service-owned failure and HTTP contracts.
The Stage 2 planned verification slices are green; residual gaps above must be
reviewed before approving the Jakarta stage. No production code changed in the
workflow or failure/HTTP slices.
