# Repository Working Agreement

## Purpose

This repository is a modernization pilot. Preserve the grocery ERP's observable
behavior while improving its build, tests, maintainability, and platform stack
through small, independently reviewable changes.

## Required workflow

1. Start from an up-to-date `main` branch.
2. Use a focused branch with the `codex/` prefix.
3. Read `.modernization/REPOSITORY_PROFILE.md` and
   `.modernization/TEST_BASELINE.md` before changing production code.
4. Run the narrowest relevant checks while developing, followed by the full
   verified build before proposing a merge.
5. Keep documentation and modernization records synchronized with discoveries.
6. Record pre-existing failures separately from regressions.

## Change boundaries

- Do not combine build repair, dependency upgrades, Jakarta migration, and
  behavior changes in one commit or pull request.
- Do not silently change REST paths, JSON contracts, validation, transaction
  boundaries, persistence mappings, or stock-movement semantics.
- Add characterization coverage before modifying poorly protected behavior.
- Do not introduce production credentials or copy local secrets into tests.
- Prefer project-native Maven commands and committed configuration.
- Avoid broad formatting or unrelated cleanup in migration commits.

## Verification expectations

Once the baseline toolchain is repaired, the minimum repository check is:

```bash
./mvnw clean verify
```

Until then, use a compatible local Maven installation with Java 17 and clearly
report that this is a fallback. A successful compile alone is not a completed
verification. Relevant REST, service, persistence, and startup behavior should
be covered as the safety net grows.

## Commit guidance

Use imperative, scoped commit messages, for example:

```text
build: restore reproducible Maven wrapper
test: characterize purchase stock movements
docs: record modernization discovery
```
