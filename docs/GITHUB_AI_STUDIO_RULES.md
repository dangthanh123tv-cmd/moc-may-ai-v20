# GitHub + Google AI Studio workflow

## GitHub

GitHub is the canonical source of truth.

Recommended branches:

- `main` — release-ready
- `moc-may-ai-v29` — known-good Android baseline
- `moc-may-ai-v31-unified` — unified upgrade work

All changes must pass Android build + static checks before release.

## Google AI Studio

Use AI Studio for:

- prompt experiments
- Gemini API prototyping
- reviewing/refactoring individual modules
- testing multimodal ideas

When importing from GitHub, explicitly select Android/native when the goal is Android.
Do not let a default web build replace the Android Gradle/CMake project.

After AI Studio changes, review the diff before syncing back to GitHub.

## Recovery rule

If AI Studio turns the project into Vite/React:

1. Stop.
2. Do not push those files into the Android branch.
3. Reset to the last known-good Android commit.
4. Apply only the intended Android changes.
