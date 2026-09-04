# PostgreSQL smoke-test evidence

Date: 2026-09-04. Platform: macOS arm64, Temurin 17.0.20.1,
Postgres.app PostgreSQL 18.6. Artifact includes the corrected `GroceryApp`
manifest entry point. This was a manual smoke test, not an automated regression.

## Isolation

A new cluster was initialized outside the repository under
`~/.local/share/SpringBootSampleERP/pgdata`, listening only on `127.0.0.1:55432`.
The dedicated database is `grocery_modernization`, owned by `ozkanogus`.
TCP authentication uses SCRAM with a generated password stored in a private local
file, not version control. No existing database was used.

The packaged JAR ran with environment overrides for the database URL, username,
password, `SERVER_ADDRESS=127.0.0.1`, and `SERVER_PORT=18082`. The database was
empty beforehand. Hibernate created seven tables and the existing DataPopulator
ran unchanged, inserting random demo transactions.

## Observed results

| Check | Result |
| --- | --- |
| Packaged application startup | Successful |
| GET /api/groceries | 200; 10 records |
| GET /api/products | 200; 10 records |
| GET /api/purchases | 200; 10 records |
| GET /api/sales | 200; 30 records |
| GET /api/stockMovements | 200; 400 records |
| GET /api/sales/topSold/1 | 200; 3 records |
| GET /api/groceries/999999 | 404 |
| GET /v3/api-docs | 200 |
| Stock movement signs | 100 positive purchase rows; 300 negative sale rows |

The application was stopped after validation. The isolated PostgreSQL server
and demo data remain available; no automatic startup service was configured.

## Limits and next checks

### Stage 1 repeat — Boot 2.7.18

On 2026-09-04 the same packaged checks passed against fresh database
`grocery_boot27` on the existing isolated cluster. All list/report counts above
matched. Missing IDs returned empty 404 responses across all five resources;
creating a grocery with an existing ID returned HTTP 400 and numeric Problem
`status: 400`. OpenAPI returned 11 paths. Table columns, types, defaults,
nullability and constraint definitions matched `grocery_modernization` exactly.
The API process was stopped; both databases remain available. See
`MIGRATION_REPORT.md` for execution details and limitations.

### Remaining limits

- Report execution succeeded, but ranking correctness, month boundaries, ties,
  and grocery isolation still need deterministic integration tests.
- An OpenAPI response was obtained; its complete schema was not audited.
- Sample values are random and should not be asserted as fixed expectations.
- The unconditional demo initializer and Hibernate schema auto-update are not
  suitable defaults for a future deployed system. Separate their remediation
  from framework upgrades.
- This does not establish full PostgreSQL 18 compatibility for every workflow.
