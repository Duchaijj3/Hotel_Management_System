# Domain Docs

This repository uses a single-context domain documentation layout.

## Before exploring

Read when present:

- `CONTEXT.md` at the repository root.
- Relevant ADRs under `docs/adr/`.

If these files do not exist, proceed silently. The domain-modeling workflow creates them when domain terms or architectural decisions are resolved.

## File structure

```text
/
├── CONTEXT.md
├── docs/
│   ├── adr/
│   └── agents/
└── backend/
```

## Domain vocabulary

Use terminology defined in `CONTEXT.md` when naming issues, tests, modules and architectural proposals.

If a required concept is missing, reconsider whether the codebase already uses another term or record it as a domain-modeling gap.

## ADR conflicts

If proposed work contradicts an existing ADR, report the conflict explicitly instead of silently overriding the decision.
