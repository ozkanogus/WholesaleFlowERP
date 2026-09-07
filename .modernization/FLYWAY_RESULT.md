# Flyway schema baseline — verified

2026-09-05. Owner approved DATABASE_MIGRATION_PLAN.md. Branch:
codex/flyway-baseline. Status: GREEN / COMPLETE. No remote push or deployment.

## Result

Flyway 11.14.1 now owns PostgreSQL schema creation. V1__initial_schema.sql creates
the established seven tables, shared sequence (increment 50), columns, primary
keys, foreign keys and product-name unique constraint without data. Hibernate's
runtime default changed from update to validate. Automatic Flyway baselining is
off, migration validation is on and clean is disabled.

Boot-managed spring-boot-starter-flyway and the required PostgreSQL database
module were added. Dependency resolution confirms flyway-database-postgresql and
flyway-core 11.14.1. H2 tests explicitly disable Flyway; the opt-in PostgreSQL
profile enables it and validates the migrated schema. The packaged test asserts
that V1 and the safe configuration are present.

## Verification

Environment: Temurin 21.0.12.1, Boot 4.0.8, Maven wrapper 3.9.16,
PostgreSQL 18.6. Working directory: /Users/ozkanogus/Projects/WholesaleFlowERP.

| Gate | Result |
| --- | --- |
| Entry PostgreSQL build | 60 pass, 9.680 s |
| First build on empty grocery_flyway_test | V1 applied; 60 pass, 11.283 s |
| Final PostgreSQL build | 60 pass: 42 Surefire + 18 Failsafe, 11.102 s |
| Final default H2 build | 44 pass: 42 Surefire + 2 Failsafe, 7.471 s |
| Final failures/errors/skips | 0/0/0 in both builds |
| Fresh schema | One successful V1 history row; seven application tables; sequence increment 50 |
| Catalog comparison | Schema-only dumps equal to grocery_config_smoke after excluding Flyway history and volatile dump headers |
| Repeat startup | Schema up to date; no migration necessary; one unchanged history row |
| Modified V1 checksum probe | Startup/test context rejected with migration checksum mismatch; original V1 restored |
| Untracked non-empty schema | Startup exit 1; rejected before history creation; seven tables and untouched sequence retained |
| Packaged runtime | Empty default start then explicit demo start: 10/10/10/30/400 records; API/report/missing-ID checks pass |
| OpenAPI | Paths and component schemas equal to the Java 21 checkpoint after key sorting |

The checksum probe was a temporary comment added with a reviewable patch and
removed immediately after the negative test. The final green builds used the
original checksum. No applied migration was repaired, edited or baselined.

Disposable databases created: grocery_flyway_test, grocery_flyway_smoke and
grocery_flyway_legacy. Retained as evidence. Existing grocery_* databases were
not altered. Runtime/test credentials came from private environment settings.
All application processes were stopped.

Local evidence: /private/tmp/flyway-entry.log, flyway-first.log,
flyway-final2-postgres.log, flyway-final2-default.log, flyway-smoke-first.log,
flyway-smoke-repeat.log, flyway-legacy-rejection.log,
flyway-checksum-rejection.log, flyway-dependencies.txt and normalized schema dumps.

## Existing database adoption

Do not set baseline-on-migrate. Before adopting an existing schema:

1. Stop application writes and take a tested database backup.
2. Compare its complete catalog with V1, including columns/types/nullability,
   constraints, keys and sequence definition. Resolve every difference explicitly.
3. Record data counts and sequence state. Run Flyway's explicit baseline at
   version 1 only after owner review, then validate; never execute V1 on that schema.
4. Start the matching artifact with Hibernate validation and demo data disabled;
   exercise API and critical workflows. Preserve backup and evidence.

This procedure is documented, not executed against any retained user database.
Because V1 represents the already established schema, explicit baseline version
1 is the adoption marker; later changes must be new immutable V2+ migrations.

## Limits and rollback

Flyway checksum/history plus Hibernate validation do not prove every live catalog
object matches; catalog comparison remains required. No down migration or schema
cleanup was added. H2 remains a separate test path. CI, security, coverage and
deployment remain separate stages.

Code rollback uses a reviewed revert and the prior artifact. It does not undo DDL.
Never re-enable automatic schema update as an emergency rollback. Restore a tested
backup or apply a separately reviewed forward migration for database recovery.
