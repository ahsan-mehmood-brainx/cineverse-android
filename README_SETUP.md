# Android PR Compliance Review Agent Setup

This setup runs two review layers on every pull request:

1. **Qodo PR-Agent** for the standard PR review and auto-description.
2. **Android Compliance Review Agent** (`scripts/android_pr_review.js`) for strict, active standards enforcement against your team's rule book (Kotlin, Jetpack Compose, and/or XML Views), with a mandatory static regex scan layered on top so obvious violations are never missed even if the model overlooks them.

The compliance agent uses **four** files as the source of truth:

```text
pr_compliance_checklist.yaml        # main code-quality sections (32 sections)
testing_compliance_checklist.yaml   # dedicated, detailed testing phase
prd_compliance_checklist.yaml       # PRD requirements + PR-vs-PRD alignment
best_practices.md                   # supporting standards + General Product Engineering Practices (A–G)
```

## Files to add

Copy these files to your Android project, **preserving these exact paths** (the workflow and script both assume this layout):

```text
.github/workflows/android_pr_agent.yaml
.pr_agent.toml
best_practices.md
pr_compliance_checklist.yaml
testing_compliance_checklist.yaml
prd_compliance_checklist.yaml
scripts/android_pr_review.js
```

> `.pr_agent.toml` (with the leading dot) belongs at the **repo root** — not inside `.github/workflows/`. Likewise `android_pr_agent.yaml` belongs inside `.github/workflows/`, not at the repo root. (An earlier version of this template had both misplaced; if you copied from that version, delete the stray root-level `pr_agent.toml`/`android_pr_agent.yaml` and the nested `.github/workflows/.pr_agent.toml` before adding the corrected files.)

## GitHub secret required

```text
Repository → Settings → Secrets and variables → Actions → New repository secret
Name: OPENAI_KEY
Value: your OpenAI API key
```

Do not commit API keys into the repo.

## How the compliance review works

The agent operates in **strict compliance mode** across four phases, each a separate structured-output call to OpenAI (JSON Schema-validated, not free-form text — so a section can never silently vanish from the response):

1. **Phase 1 (highest priority):** `pr_compliance_checklist.yaml` — all 33 checklist sections (32 code-quality sections + `ai_generated_code_quality`), evaluated section by section against the PR diff.
2. **Phase 2:** `best_practices.md` — General Product Engineering Practices (Sections A–G: architecture, security, scalability, performance, code structure, accessibility/localization/observability, and reviewer autonomy).
3. **Phase 3 (mandatory):** `testing_compliance_checklist.yaml` — TDD discipline, unit tests, model/serialization tests, Compose/View UI tests, screenshot/visual tests, integration/functional tests, API/network tests, edge cases, and coverage/CI.
4. **Phase 4 (important):** `prd_compliance_checklist.yaml` — PRD/ticket reference presence, PRD structure quality, PR-vs-PRD alignment, and deviation/change control.

One checklist section (`pr_size_lines_of_code`) is evaluated automatically, not by the model: the script counts actual added+removed lines in the diff and flags it if the total exceeds **1500 lines** (configurable via the `MAX_PR_LINES` env var on the workflow step).

A regex-based static scan also runs over the diff's added lines in `.kt`/`.kts` files and `.xml` files under `res/` (non-null assertions `!!`, unsafe `as` casts, `runBlocking`, `GlobalScope.launch`, manual DI-managed object construction, `Retrofit.Builder()` inline, hardcoded Compose/XML colors, hardcoded XML/Compose strings and concatenation, string-interpolated navigation routes, leaked `Context` properties, inline collection transforms inside `LazyColumn`/`LazyRow` `items()`, `@Suppress("DEPRECATION")`, `findViewById` instead of ViewBinding, `println`, hardcoded string comparisons, raw `Map<String, Any?>`/`JSONObject`, empty catch blocks, hardcoded secrets, `notifyDataSetChanged()`, leftover AI narration comments, and trivially-true test assertions), and those findings are merged into the relevant section so they're never missed even if the model overlooks them. This static scan only feeds Phase 1/2 sections — Phase 3 and 4 findings come entirely from the model reading the diff, PR title, and PR description.

The posted PR comment starts with an **Evaluation Summary** made of four tables, one per checklist:

| Sr. | Section ID | Violations |
| ---: | --- | ---: |
| 1 | `title_description` | 0 |
| 2 | `state_management` | 2 |
| ... | ... | ... |

Then detailed per-phase, per-section output includes:

1. **Violations Found** — every deviation from standards, with file names and line numbers when possible
2. **Suggested Fixes** — clear, actionable recommendations aligned with project standards (for Phase 3, this names the exact test to add and what it should assert)
3. **Violation Count** — total violations detected in that section

Sections that do not apply to the PR changes show `N/A` in the summary table.

There is **no scoring**, grading, percentage-based evaluation, pass/fail rating, or weighted compliance score.

The compliance review is posted/updated as a single PR comment titled **Android PR Compliance Review** (the script edits the existing comment in place via the GitHub API on re-runs, rather than deleting and reposting). This review is independent from Qodo's native review output.

## What this checklist assumes

- **Supports both XML Views and Jetpack Compose** — sections like `databinding_viewbinding_usage` apply to XML-backed screens, `compose_performance_recomposition`/`immutability_and_efficient_state` apply to Compose, and most sections (architecture, error handling, DI, naming, etc.) apply to either. Not applicable to a Java-only project (see the note on Section 25 of `best_practices.md`).
- **DI-framework-agnostic**: works with Hilt, Koin, Dagger, or manual DI — the reviewer follows whatever the project has established.
- **Test-framework-agnostic**: works with JUnit4 or JUnit5, MockK or Mockito, Espresso or Robolectric — whichever the project has standardized on.
- **No project-specific stack section**: unlike a single-repo setup, this template has no "approved stack" section (no fixed DI framework, networking library, or module layout pinned) — it's designed to be copied into any Android repo as-is.

## `[ignore]` glob in `.pr_agent.toml`

`.pr_agent.toml` excludes `**/*Test.kt`, `**/*Test.java`, and `androidTest/`/`test/` source sets from Qodo's own review pass, so large test files don't eat its diff-token budget and silently push other changed files out of review. This does **not** exempt test files from Phase 3 (Testing Compliance) — `scripts/android_pr_review.js` reads the full `git diff` directly in the CI runner, independent of Qodo's config.

## Change control

The agent does **not** automatically modify code, generate commits, or apply fixes. It only reviews the PR, identifies violations, and provides recommendations.

## Important branch note

For PRs like `feature/my-branch → main`, the workflow file must exist in the base branch (`main`/`master`/`dev` — the workflow's `branches:` filter and the script's `BASE_REF` env var both need to match your default branch) for GitHub Actions to run reliably.

Recommended:

1. Add this setup to your default/base branch.
2. Merge/rebase your feature branch from it.
3. Push a new commit to the PR branch to re-run the checks.

## Re-running the review

- **"Re-run all jobs"** in the GitHub UI replays the original event, so the review runs again every time you click it.
- **Reopening a closed PR** fires `pull_request: reopened` and re-runs the review automatically.
- Editing the PR title/description fires `pull_request: edited` and re-runs (useful after adding a PRD link).
- **Manual run:** Actions tab → this workflow → *Run workflow* → enter the PR number (`workflow_dispatch`).
- Each run **updates the existing compliance comment in place** (PATCH, not delete+repost), so the PR always shows the latest review.

## Configuration knobs

- `OPENAI_MODEL` in the workflow — defaults to `gpt-4.1-mini`. Both the Qodo step (`config.model`) and the compliance script are pinned to this model. If your OpenAI project only has access to certain models, set all three to a model you can call (a `model_not_found` / 403 error means the project lacks access to the chosen model, not a key problem).
- `MAX_PR_LINES` in the workflow — defaults to `1500` (see Phase 1, `pr_size_lines_of_code`).
- `num_code_suggestions` (default `4`) in `.pr_agent.toml`.
