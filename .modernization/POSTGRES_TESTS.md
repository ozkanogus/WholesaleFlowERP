# PostgreSQL integration tests

Stage 2 reporting slice, approved 2026-09-04. Framework remains Boot 2.7.18.

## Isolated setup

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
export GROCERY_TEST_DB_URL=jdbc:postgresql://127.0.0.1:55432/grocery_stage2_test
export GROCERY_TEST_DB_USER=ozkanogus
# Set GROCERY_TEST_DB_PASSWORD from your private local credential store.
./mvnw -B -ntp -Pdev,postgres-tests clean verify
```

Keep `dev` explicitly enabled to preserve the baseline dependency profile.
The regular `./mvnw clean verify` excludes PostgreSQL tests intentionally and
still runs the original 30 tests. The opt-in command adds four report tests;
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

Direct SQL fixtures exercise the real repository query, not service persistence,
cascades, DTO mapping or HTTP serialization. Those are subsequent Stage 2 work.

## Evidence and limitations

The unchanged 30-test baseline passed before editing. The initial fixture used
the wrong sequence name; correcting it to the existing `sequence_generator`
made the first three PostgreSQL tests pass. This was a test setup defect, not
an application regression.

The new tie test also happened to pass against the original query. That result
does not guarantee ordering: the original SQL specified no tie-break. The query
now explicitly orders both the limited subquery and outer result. No changes to
quantity aggregation, month selection, joins or grocery filtering were made.

Stage 2 is still in progress: purchase/sale cascades, stock replacement/deletion,
transaction rollback and broader DTO/error contracts remain outstanding.
