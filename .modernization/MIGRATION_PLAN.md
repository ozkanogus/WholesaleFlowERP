# Proposed migration plan

Prepared: 2026-09-04. Owner/approver: repository owner.
Planning does not authorize execution. Stage 1 was approved and verified on
2026-09-04. Stages 2, 3a–3e and 4a–4d are verified; see BOOT40_RESULT.md.
Historical baseline details below describe the original planning checkpoint.

Next proposed stage: [JDK upgrade assessment](JDK_UPGRADE_PLAN.md), targeting
Java 21 LTS as selected by the owner. Execution was approved and is paused at
the Modernizer bytecode compatibility gate; see JAVA21_RESULT.md. The verified
runtime remains Java 17 / Boot 4.0.8. Java 25 is deferred.

## Current state and target

Java 17, Spring Boot 2.7.18, Maven 3.9.16; 30 automated tests pass.
The pre-stage framework baseline was Spring Boot 2.6.3.
Packaged startup and API smoke tests passed against isolated PostgreSQL 18.6.
See `TEST_BASELINE.md` and `POSTGRES_SMOKE_TEST.md` for limitations.

Recommended route: retain Java 17 while crossing framework boundaries, reach
Boot 3.5 as a compatibility checkpoint, then assess a maintained Boot 4.x line
as the eventual destination. Recheck exact patch releases and support status
before each stage. Intermediate versions are migration checkpoints, not approved
deployment targets. Do not combine a new JDK target with the Jakarta transition.

Boot 3.5 is a viable temporary engineering stopping point if Boot 4 dependency
compatibility remains unresolved, not an assertion of current OSS support.
Direct 2.6-to-4 migration is rejected because it combines Jakarta, persistence,
serialization, server, and test-module transitions. A later Java LTS upgrade
requires its own processor/tool compatibility assessment and approval.

## Evidence and compatibility assessment

Sources accessed 2026-09-04:

- [Boot 3 preparation](https://spring.io/blog/2022/05/24/preparing-for-spring-boot-3-0/)
- [Boot 3 migration guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)
- [Boot 3.5 requirements](https://docs.spring.io/spring-boot/3.5/system-requirements.html)
- [Boot 4 migration guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Springdoc documentation](https://springdoc.org/)
- [Problem Spring Web project](https://github.com/zalando/problem-spring-web)

| Area | Assessment | Status / required evidence |
| --- | --- | --- |
| Java | Java 17 satisfies Boot 3.5 and Boot 4.0 minimums | Confirmed by Spring documentation; application compatibility still needs tests |
| Framework | Step through 2.7 before Jakarta; through 3.5 before Boot 4 | Confirmed migration-guide prerequisites |
| Build | Maven wrapper works; compiler/processors and test plugins are old | Confirmed locally; target plugin/processor versions Unknown until resolved |
| Persistence | Hibernate/Jakarta changes affect imports, dialects, naming and generated SQL | Inference from current code and major-version boundary; PostgreSQL regression suite required |
| Serialization | Hibernate 5 Jackson module and JAXB module need review | Confirmed current dependencies; target combination Unknown |
| OpenAPI | Existing Springdoc 1.6.5 cannot simply be carried across all targets | Target coordinates/versions must be selected from its compatibility matrix |
| Errors | Zalando ProblemHandling and Jackson modules couple error contracts to libraries | Boot 4 compatibility Unknown; retain contract tests before deciding replacement |
| Embedded server | Stage 4b removed redundant Undertow; Tomcat is verified | Confirmed; see `TOMCAT_RESULT.md` |
| Security/messaging | No security layer or messaging found | Confirmed discovery; new features out of migration scope |
| Tests | 30 green tests; PostgreSQL reporting only smoke-tested | Confirmed; deterministic SQL and transaction coverage remains a gate |
| Delivery | No deployed system, CI or container runtime | Confirmed; deployment and infrastructure redesign deferred |

## Migration graph

```text
2.6.3 / Java 17 / green baseline
  -> Stage 1: Boot 2.7 bridge
  -> Stage 2: deterministic database and contract gates
  -> Stage 3: Jakarta / Boot 3, then minor-line checkpoints to 3.5
  -> compatibility decision: server, error library, serialization, processors
  -> Stage 4: Boot 4 candidate
```

## Shared stage contract

For every stage: branch from clean local main using `codex/`; preserve original
failure evidence; run `./mvnw clean verify`; never weaken assertions to make an
upgrade green. Merge only verified work. No remote publication is authorized by
this plan. Roll back code with a reviewed revert of that stage, never a destructive
reset. Use a fresh isolated PostgreSQL database for each framework comparison;
do not apply schema auto-update to data intended to survive rollback.

No OpenRewrite recipe is yet approved. If used, first pin its version, review its
scope and dry-run diff, and validate the result with the same tests. Small POM
edits and repository-specific corrections can be reviewed directly.

### Stage 1 — Boot 2.7 bridge

- Status: GREEN / COMPLETE; approval owner/date: owner / 2026-09-04.
- Evidence: `MIGRATION_REPORT.md`; 30 unchanged tests pass and fresh-database
  packaged smoke checks pass with matching columns and constraints.
- Purpose/rationale: establish the prerequisite baseline for Boot 3.
- Scope: parent and explicit Boot version to 2.7.18; only directly required
  compatibility fixes. Keep Java 17 and the Maven wrapper unchanged.
- Out of scope: Jakarta, business behavior, schema redesign, broad cleanup.
- Entry: owner approval and unchanged green baseline.
- Approach: explicit POM edits; inspect effective dependency changes and
  deprecation warnings. Pin any newly required unmanaged versions with evidence.
- Verify: `./mvnw clean verify`, then repeat packaged PostgreSQL smoke checks
  in a fresh database; compare HTTP/error contracts and table mappings.
- Risk: transitive changes reveal untested behavior. Stop on unexplained failure.
- Rollback: revert this stage and retain the preceding artifact; discard only
  explicitly identified disposable test data after approval.

### Stage 2 — Close high-risk verification gaps

- Status: GREEN / COMPLETE; approval owner/date: owner / 2026-09-04.
- Reporting slice verified: ranking, month edges, grocery isolation and empty
  results. Owner approved product-ID ascending tie ordering. See `POSTGRES_TESTS.md`.
  Six workflow cases also verify single-line persistence, replacement, deletion
  cascades, DTO reload and surrounding-transaction rollback. Six further cases
  cover service-owned foreign-key failure rollback and full-context HTTP/JSON
  contracts. Full opt-in build: 46 passed, no skips. Residual coverage gaps in
  `POSTGRES_TESTS.md` require review before Stage 3 approval.
- Purpose/rationale: make regressions distinguishable before changing Hibernate.
- Scope: deterministic PostgreSQL tests for purchase/sale persistence and
  cascades, stock replacement/deletion, rollback, and report ordering/month
  boundaries. Expand DTO/error contracts. Isolate demo seeding for test execution.
- Out of scope: new wholesale features, authentication, framework upgrades.
- Entry: Stage 1 green; isolated database and agreed intended report rules.
- Approach: explicit test fixtures and rollback; record ambiguous business rules
  rather than asserting guesses. Separate any confirmed defect fixes.
- Verify: `./mvnw clean verify` plus a documented PostgreSQL integration command
  that fails when its database prerequisites are absent; no silent skip claims.
- Risk: existing demo randomness and incomplete semantics. Owner resolves material
  business ambiguities before expectations become contracts.
- Rollback: revert tests/configuration independently of business fixes.

### Stage 3 — Jakarta and Boot 3 compatibility checkpoint

Stage 3e (Boot 3.5.16 / Springdoc 2.8.17) approved and GREEN on 2026-09-04.
See `BOOT35_RESULT.md` for scope, evidence and rollback. Earlier checkpoint
reports remain historical evidence. Boot 4 execution remains unapproved.

- Status: GREEN; Stages 3a–3e (Boot 3.1–3.5) completed on 2026-09-04.
  See the linked checkpoint reports. Stage 4 still requires compatibility review.
- Stage 3a proposal: Boot 3.1.12 / Java 17, with exact candidate dependency
  changes and residual test risks in `STAGE3_COMPATIBILITY.md`. This first
  checkpoint is proposed to avoid a temporary Jackson override on Boot 3.0.
  Target selection and acceptance of disclosed gaps still require approval.
- Purpose/rationale: isolate the Java EE-to-Jakarta and Hibernate transition.
- Scope: Boot 3 entry checkpoint, relevant javax persistence/validation imports,
  Hibernate dialect/naming, Jackson Hibernate integration, Springdoc coordinates,
  compatible error handling, processors and test/build APIs. Then separate
  verified minor-line steps to 3.5 using each release's migration notes.
- Out of scope: Boot 4, JDK upgrade, business/schema redesign. Do not rename
  Java SE javax packages indiscriminately.
- Entry: Stage 2 green; exact unmanaged dependency compatibility recorded and
  approved. Each minor-line substage gets its own reviewed scope.
- Approach: evaluate maintained deterministic recipes; handle residual mapping
  and configuration differences explicitly. Remove temporary property migrator
  before declaring completion.
- Verify: shared build and deterministic PostgreSQL suite; compare JSON/error
  responses, report results, schema and packaged startup at every checkpoint.
- Risk: unmanaged dependencies or changed ORM behavior block progress.
- Rollback: revert only the failing substage, restore prior artifact/test database.

### Stage 4 — Boot 4 candidate, conditional

Final update: Stage 4d is approved and GREEN on Boot 4.0.8 / Springdoc 3.0.3,
Java 17. 56 full-profile / 40 default tests and runtime/schema/OpenAPI checks pass.
See BOOT40_RESULT.md. The original proposed scope below remains historical.

Preparation review: see `BOOT4_PREPARATION.md` for repository evidence and
separately gated steps 4a–4d. Stage 4a is GREEN; see
`BOOT4_TEST_PREPARATION_RESULT.md`. Stage 4b is GREEN in `TOMCAT_RESULT.md`.
Stage 4c is GREEN in ERROR_HANDLER_RESULT.md. Framework execution remains unapproved.

Exact Stage 4c proposal: `ERROR_HANDLER_PLAN.md`; includes broader advice
characterization before removal and a compatibility wire model. Execution verified;
unexpected-500 redaction was separately approved. Next: exact Stage 4d proposal.

Stage 4d candidate scope is now in BOOT4_TRANSITION_PLAN.md: Boot 4.0.8 /
Springdoc 3.0.3, Java 17 unchanged. It is a proposal, not execution approval;
the documented entry checks and API/documentation review still apply.

- Status: PROPOSED; approval owner/date: owner / pending.
- Purpose/rationale: reach an appropriately maintained framework line after the
  prerequisite 3.5 checkpoint, rather than stopping indefinitely on a bridge.
- Scope: select maintained Boot 4.x patch; server replacement if required,
  modular starters/tests, Jackson transition, and compatible OpenAPI/error stack.
- Out of scope: new domain features, production deployment, simultaneous JDK leap.
- Entry: Stage 3 green; exact dependency matrix and server/error-library decisions
  resolved and explicitly approved. This is not execution-ready today.
- Approach: split server/library preparation from framework edits where possible;
  retain externally observed contracts and justify every changed expectation.
- Verify: full build, PostgreSQL suite, packaged startup and OpenAPI/error checks.
- Risks: Undertow removal; unsupported third-party adapters; changed serialization.
- Rollback: stage revert and previous artifact against an untouched test database.

## Execution rule

Only execute the next explicitly approved stage. Stop when it is red or evidence
is inconclusive. Approval of Stage 1 does not approve later targets, business
redesign, library replacement, or release/deployment.
