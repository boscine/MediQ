# Project Map

## Android (`app/`)
- `MainActivity.kt` — entry point, hosts Compose + bottom bar.
- `ui/theme/` — Color.kt, Type.kt, Spacing.kt, Shape.kt (design tokens; only place for raw color).
- `ui/components/` — MediQBottomBar (reference), MediQBackHeader, MediQNotice, MediQStatusMark.
- `ui/navigation/` — Routes.kt, MediQNavHost.kt.
- `ui/screens/` — one package per screen family:
  - `home/` — HomeScreen (reference implementation of token system).
  - `doctors/`, `doctordetails/` — list + detail, search empty state.
  - `booking/` — booking flow.
  - `appointments/`, `appointmentdetails/` — tracking + manage one booking.
  - `auth/` — SignIn + 4-step Register.
  - `messages/`, `secretarychat/` — secretary messaging.
  - `profile/` — account + health data.
  - `notifications/` — updates list.
- `ui/screens/*` follow pattern: `*Screen.kt`, `*UiState.kt`, `*States.kt`, `*Previews.kt`, `*Samples.kt`.

## Web frontend (`mediqweb/mediqweb-fe/`)
- Angular 21 app. `src/` components, `public/` assets. Config: angular.json, tsconfig*.json, .prettierrc.

## Web API (`mediqweb/mediqapp-api/`)
- Hono on Node (TypeScript). `src/` (index.ts entry).

## Ktor server (`server/`)
- Standalone Gradle project. `build/install/server` runnable. Tests in `server/build/.../test`.

## Supabase (`supabase/`)
- Postgres schema/migrations.

## Docs (`docs/`)
- `design-contract.md` — UI quality bar and increment order.
- `project-map.md` — this file.
