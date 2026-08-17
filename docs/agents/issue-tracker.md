# Issue tracker: GitHub

Issues and PRDs for this repository live as GitHub issues. Use the `gh` CLI for all operations.

## Conventions

- Create: `gh issue create --title "..." --body "..."`
- Read: `gh issue view <number> --comments`
- List: `gh issue list --state open`
- Comment: `gh issue comment <number> --body "..."`
- Apply/remove labels: `gh issue edit <number> --add-label "..."` or `--remove-label "..."`
- Close: `gh issue close <number> --comment "..."`

Infer the repository from `git remote -v`; `gh` does this automatically when run inside the clone.

## Pull requests as a triage surface

**PRs as a request surface: no.**

GitHub Issues are the primary request and triage surface.

## Skill operations

When a skill says “publish to the issue tracker”, create a GitHub issue.

When a skill says “fetch the relevant ticket”, run:

`gh issue view <number> --comments`

## Wayfinding operations

- A map is a GitHub issue labelled `wayfinder:map`.
- Child tickets are linked as sub-issues where supported.
- Blocking relationships use GitHub native issue dependencies.
- Claim a ticket with `gh issue edit <number> --add-assignee @me`.
- Resolve by commenting with the result and closing the issue.
