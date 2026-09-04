# Versioned PostgreSQL schema plan

2026-09-04. Status: PROPOSED; approve the Flyway approach before implementation.
No database writes, dependencies or runtime defaults changed during assessment.

## Target and rationale

Use Boot 4.0.8's managed Flyway 11.14.1 through spring-boot-starter-flyway and
org.flywaydb:flyway-database-postgresql. Keep Java 21 and all other versions.
Use a reviewed V1__initial_schema.sql to reproduce the established PostgreSQL
schema, then make Hibernate validate rather than update it. SQL is appropriate
because PostgreSQL is the sole application runtime database and native SQL is
already part of the application. Liquibase is a viable alternative, but no
existing changelog or installed Liquibase engine needs preserving; introducing
another changelog representation provides no demonstrated benefit here.

Evidence: current POM/BOM, repository profile and verified 44/60-test baseline;
read-only catalog inspection of grocery_config_smoke; historical schema-only
dump of grocery_boot27 used by every framework comparison. Seven tables, one
shared sequence with increment 50, existing primary/foreign/unique constraints.
Preserve timestamp types, numeric precision, identifiers and key ordering.
Do not insert demo records or reset sequences in V1.

## Scope and safeguards

- Add the managed Flyway dependencies and PostgreSQL SQL migration.
- Runtime defaults: Hibernate validate, Flyway clean disabled, automatic
  baseline-on-migrate disabled, migration validation enabled.
- An empty new database is initialized by V1, then validated by Hibernate.
- An existing non-empty schema without migration history must fail safely.
  Never enable automatic baselining to bypass that failure. Adoption requires
  a backup, schema-equivalence review and separately approved explicit baseline.
- Use new disposable databases for fresh-schema and legacy-copy tests. Existing
  grocery_* databases remain untouched; no drop, clean, repair or sequence reset.
- Keep default H2 tests separate from PostgreSQL-specific SQL. Explicitly disable
  Flyway for H2; enable it for the PostgreSQL integration profile and ensure that
  profile exercises migration-created schemas, not only restored schemas.
- Never weaken existing tests to accommodate migrations. No domain redesign,
  schema cleanup, indexes, CI, security policy or deployment changes in this stage.

## Verification gates

1. Repeat the green entry build with explicit Java 21 and private DB settings.
2. Verify effective Flyway version and database-module resolution; review SQL
   extracted from the schema dump, excluding psql directives, ownership, session
   settings and data. Preserve all application DDL semantics.
3. Migrate a brand-new empty PostgreSQL database. Assert one successful V1 record,
   seven application tables and the expected sequence; compare catalog structure
   with the verified schema, excluding Flyway's history table.
4. Run migration again and verify it is a no-op with unchanged data and sequence
   state. Exercise checksum validation using an isolated test resource/copy;
   never edit an applied migration in a retained database.
5. Test rejection of a non-empty untracked schema on a disposable legacy copy.
   Verify representative rows and sequence state are unchanged after rejection.
6. Run ./mvnw clean verify and ./mvnw -Pdev,postgres-tests clean verify.
   Require all existing cases, plus migration checks, with no unexpected skips.
7. Smoke the packaged application against the fresh migrated database, first
   without demo data and then with explicit opt-in. Check HTTP/error contracts,
   OpenAPI, persistence workflows and restart without duplicate migration work.
8. Update README, AGENTS, test baseline and migration report; merge only green
   work locally. No remote push or deployment.

## Risks and rollback

Application-level compatibility of the exact Flyway/JDK/PostgreSQL combination
is not yet tested. Stop on a dependency mismatch, unsupported-version warning,
unexplained schema difference or failing gate. Do not silently upgrade the BOM.
H2 success is not proof of PostgreSQL migration correctness.

Legacy adoption is deliberately not automatic. This stage must document the
review/backup/baseline procedure, not run it against retained user databases.
Migration checksums validate migration history, not all live-schema drift;
Hibernate validation also does not check every index or constraint. Retain the
catalog comparison as a distinct verification gate.

Rollback code through a reviewed revert, retaining external credentials and
demo opt-in. Do not automatically restore Hibernate update. A code revert does
not undo database DDL. Keep the prior artifact and database backups; recovery
against any retained database requires an explicit decision. Disposable migration
databases can be retained as evidence rather than deleted.

## Primary sources checked 2026-09-04

- [Boot 4.0.8 database initialization](https://github.com/spring-projects/spring-boot/blob/v4.0.8/documentation/spring-boot-docs/src/docs/antora/modules/how-to/pages/data-initialization.adoc)
- [Flyway PostgreSQL module and support](https://documentation.red-gate.com/flyway/reference/database-driver-reference/postgresql-database)
- [Automatic baseline safety](https://documentation.red-gate.com/fd/flyway-baseline-on-migrate-setting-277578974.html)
- [Flyway configuration settings](https://documentation.red-gate.com/fd/flyway-namespace-277578913.html)
