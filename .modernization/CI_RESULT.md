# CI automation

2026-09-05. Branch: codex/ci-java21-postgres.
Status: CONFIGURATION VERIFIED LOCALLY / HOSTED RUN NOT YET OBSERVED.

## Workflow

`.github/workflows/ci.yml` runs for pull requests, pushes to main and manual
dispatch. Permissions are read-only and concurrent runs for the same ref cancel
older runs. Both jobs use Temurin 21 and the committed Maven wrapper:

- default-build runs `./mvnw -B -ntp clean verify`;
- postgres-build starts an isolated PostgreSQL 18 service and runs
  `./mvnw -B -ntp -Pdev,postgres-tests clean verify` against an empty database.

Flyway therefore creates V1 in the PostgreSQL job before Hibernate validation and
the existing integration tests. The database name ends in `_test`, satisfying the
repository's safety check. Its username/password are explicit disposable service
values, not repository or production credentials. No demo-data flag is enabled.

Third-party actions are pinned to immutable commits with version comments:

- actions/checkout v7.0.1: 3d3c42e5aac5ba805825da76410c181273ba90b1
- actions/setup-java v6.0.0: dd06d9cba3e5552c54d9f8ea23572deb30010f7c

Checkout does not persist credentials. Maven dependency caching is handled by
setup-java. Job timeouts are 15 and 20 minutes. No write token, deployment,
artifact publication or external secret is required.

## Verification

Actionlint 1.7.12 was downloaded from its official GitHub release, SHA-256
verified (`aba9ced2dee8d27fecca3dc7feb1a7f9a52caefa1eb46f3271ea66b6e0e6953f`),
and reports no issue for the workflow. Action commit references were resolved
through the GitHub API. A preliminary Ruby validator failed because this macOS
Psych version lacks safe_load_file; that is a local validation-tool limitation,
not a workflow failure. Dedicated actionlint validation supersedes it.

The exact commands were repeated locally on Java 21:

| Check | Result |
| --- | --- |
| Default workflow command | 44 pass: 42 Surefire + 2 Failsafe, 6.985 s |
| PostgreSQL workflow command | 60 pass: 42 Surefire + 18 Failsafe, 10.039 s |
| Failures/errors/skips | 0/0/0 in both builds |
| Workflow static validation | actionlint PASS |

Local PostgreSQL used grocery_flyway_test on loopback port 55432 with private
environment settings. Local logs: /private/tmp/ci-default.log and ci-postgres.log.

## Remaining gate

The workflow cannot run on GitHub until these local commits are pushed. No push
is authorized or performed by this stage, so hosted runner/container/cache
execution is NOT RUN and the CI status is not represented as green. After push,
observe both jobs on a feature pull request and main; diagnose any environment
difference before requiring the checks in branch protection.

Rollback is a reviewed revert of the workflow commit. It changes no application,
schema or external system. Future action upgrades must update immutable pins only
after release review and validation. CI is not deployment authorization.
