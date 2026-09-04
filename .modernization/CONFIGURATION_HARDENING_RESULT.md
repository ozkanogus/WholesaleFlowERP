# Configuration hardening

2026-09-04. Approved by the owner's request to proceed with remaining work.
Scope: external runtime database settings and opt-in demo initialization.
Branch: codex/configuration-hardening. Status: GREEN.

## Changes

- Removed bundled runtime URL, username and password. Spring's standard external
  SPRING_DATASOURCE_* configuration supplies them; test-only H2 settings remain.
- DataPopulator is conditional on grocery.demo-data.enabled=true, absent by
  default. This is an intentional startup behavior change; sample generation
  logic and business services are unchanged.
- Existing tests explicitly enable the bean before enforced Mockito replacement.
  Three new context tests cover default/false/true registration; a packaged-JAR
  guard checks that connection values and a demo opt-in are not bundled.
- Ignore .env files and document external configuration and safe demo usage.

## Evidence

Java 21.0.12.1, Boot 4.0.8, Maven wrapper 3.9.16, PostgreSQL 18.6.
Working directory: /Users/ozkanogus/Projects/SpringBootSampleERP.

| Check | Result |
| --- | --- |
| Entry ./mvnw -B -ntp clean verify | 40 pass, 7.187 s |
| ./mvnw -B -ntp -Pdev,postgres-tests clean verify | 60 pass: 42 Surefire + 18 Failsafe, 10.736 s |
| ./mvnw -B -ntp clean verify | 44 pass: 42 Surefire + 2 Failsafe, 7.319 s |
| Failures/errors/skips in final builds | 0/0/0 |
| Packaged default startup with external credentials | HTTP groceries empty; all five aggregate/stock table counts zero |
| Packaged opt-in startup | 10 groceries, 10 products, 10 purchases, 30 sales, 400 stock movements |
| Packaged startup without connection settings | Exit 1; missing datasource URL, no embedded database fallback |

Runtime checks used grocery_config_smoke, a new schema-only copy of grocery_boot27,
ddl-auto=validate, private credentials, loopback port 18082. Both processes were
stopped. No prior database was changed. The PostgreSQL test suite used grocery21_test.

Initial new context test lacked ProductMapper, which Spring injects into the
registered SaleService mock. Added the missing fixture dependency; no production
change or assertion weakening was needed. Preserved diagnostic: config-postgres.log.
Final local logs under /private/tmp: config-entry.log, config-postgres-final.log,
config-default-final.log, config-default-smoke.log, config-enabled-smoke.log and
config-missing-connection.log.

## Limits and follow-up

This does not rotate any reused credential or erase Git history. Previously
committed passwords must be treated as exposed. No history rewrite or remote push.
Demo initialization remains unsuitable for partially seeded or real databases.
Schema auto-update remains unchanged pending the separate versioned-migration
stage. CI, broader coverage, authentication/authorization and deployment remain.
No new framework/library version, schema change or business rule was introduced.

Rollback: reviewed code revert, but keep credentials external and demo disabled
unless intentionally returning to disposable demo behavior. Never reintroduce a
reused password. Do not undo database changes by deleting existing user data.
