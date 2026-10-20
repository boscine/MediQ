# MediQ

Multi-surface healthcare app: Android client (Jetpack Compose), Angular web client, and a Ktor/Hono backend. Treat each surface as its own subproject.

## Tech Stack

- Android: Kotlin 2.x, Jetpack Compose, Material 3, Navigation Compose, min SDK 24, compile/target SDK 37, Java 11. Gradle (Kotlin DSL).
- Web: Angular 21 (TypeScript 5.9), RxJS, Chart.js; npm.
- API (web): Hono on Node, TypeScript, tsx.
- Backend (Android-facing): Ktor 3.1 (Kotlin), H2/PostgreSQL via HikariCP, JWT auth; Gradle.
- DB: Supabase (Postgres) for some surfaces.

## Commands

Android (from repo root):
- Build debug: `./gradlew :app:assembleDebug`
- Tests: `./gradlew :app:test`
- Lint: `./gradlew :app:lint`

Web frontend (in `mediqweb/mediqweb-fe`):
- Dev: `npm start` (ng serve)
- Build: `npm run build`
- Test: `npm test`

Web API (in `mediqweb/mediqapp-api`):
- Dev: `npx tsx src/index.ts` (or npm script if present)

Ktor server (in `server`):
- Standalone Gradle build (own `settings.gradle.kts` + version catalog). Run from `server/`:
  - Build: `gradlew.bat build` / `gradlew.bat build -x test`
  - Run: `gradlew.bat run` — listens on **8099**
  - Tests: `gradlew.bat test`
- Demo account: `demo_patient` / `demo12345`

## Running the app against the API

The app talks to the API on the device's own loopback, forwarded to the developer
machine by adb. Run this once per device/emulator, after it connects:

```
adb reverse tcp:8099 tcp:8099
```

`10.0.2.2` (the emulator's host alias) also reaches the machine, but the host
firewall blocks inbound connections to a server with no allow-rule for it, so
the adb forward is the path that works. It behaves the same on an emulator and
on a physical handset. Without it, sign-in reports "We could not reach MediQ".


## Code Conventions

- Android UI lives under `app/src/main/java/com/example/mediq/ui`. Follow the design contract in `docs/design-contract.md`. Token-only styling: no `Color(0x…)` or inline `fontSize` outside `ui/theme`.
- Compose screens split into stateless UI + UiState; previews in `*Previews.kt`, samples in `*Samples.kt`.
- Named exports / files named after their primary composable. Keep files under ~200 lines (split when exceeded).
- Web: Angular standalone components, RxJS for async, Prettier formatting (see `.prettierrc`).
- Server: Ktor routing in `http/Routes.kt`, DTOs with kotlinx.serialization, services under `service/`.

## Boundaries

- Never commit `.env`, `local.properties`, or secrets.
- Do not add dependencies without checking bundle/size impact.
- Ask before modifying database schema or Supabase migrations.
- Android UI changes must keep `:app:assembleDebug` green and stay within their design-contract slice.
- Do not touch screens outside the current increment (design contract §6).

## Project Map

See `docs/project-map.md`.

## Patterns (reference)

- Token-driven component: `app/.../ui/components/MediQBottomBar.kt`.
- Design tokens: `app/.../ui/theme/{Color,Type,Spacing,Shape}.kt`.
- Navigation: `app/.../ui/navigation/{Routes,MediQNavHost}.kt`.
