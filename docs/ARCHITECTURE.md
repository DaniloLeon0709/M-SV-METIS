# VitaAlert Architecture

This document describes the module boundaries, Clean Architecture layering, and the data flows for authentication and vitals monitoring.

## Module responsibilities

| Module | Responsibility |
| --- | --- |
| `:app` | Application entry point, Compose UI/navigation, DI wiring, workers and services orchestration. |
| `:designsystem` | Theme, colors, typography, and shared UI components. |
| `:domain` | Pure Kotlin business models and services (no Android dependencies). |
| `:data` | Local persistence (Room), data mappers, repository implementations. |
| `:auth` | Authentication models, token storage, session management, and auth network stubs. |
| `:ble` | BLE scanning, GATT client, demo vital source, monitoring service integration. |
| `:core` | Shared utilities and base abstractions. |
| `:reports` | Stub module for future reporting features. |

## Clean Architecture layers

```
                +------------------+
                |       UI         |  (:app, :designsystem)
                +------------------+
                         |
                         v
                +------------------+
                |    Use Cases     |  (:domain services)
                +------------------+
                         |
                         v
                +------------------+
                |  Data Access     |  (:data, :auth, :ble)
                +------------------+
                         |
                         v
                +------------------+
                | Framework/Device |  (Room, BLE, WorkManager, Security)
                +------------------+
```

**Rules**
- `:domain` depends on nothing else (pure Kotlin).
- `:data`, `:auth`, and `:ble` depend on `:domain` to implement repositories and services.
- `:app` depends on all feature modules and composes them via DI.

## Auth flow

```
User -> Login UI
  |
  v
LoginViewModel
  |
  v
AuthRepository (Fake or Network)
  |
  v
TokenStorage (EncryptedSharedPreferences)
  |
  v
SessionManager (StateFlow)
  |
  v
UI reacts to Authenticated/Unauthenticated/Refreshing
```

**Notes**
- Runtime uses `FakeAuthRepository` with fixed credentials.
- Unit tests use MockWebServer for `/auth/login` and `/auth/refresh`.

## BLE/Demo -> Room -> UI flow

```
BLE Scanner / DemoVitalSource
          |
          v
   MonitoringService
          |
          v
    VitalRepository
          |
          v
  Room (VitaAlertDatabase)
          |
          v
Flow -> ViewModel -> Compose UI
```

**Notes**
- Demo mode produces HR/SpO2 every 1s, Temp every 45s, and BP on demand.
- UI subscribes to `Flow` queries for latest readings and chart data.

## Key design decisions

- **Offline-first**: Room is the source of truth for vital readings.
- **Separation of concerns**: domain models live in `:domain`, while Android/IO details stay in `:data`, `:auth`, and `:ble`.
- **Demo-first development**: demo vital source drives the UI without a backend.
- **Security**: token persistence uses encrypted shared preferences.
- **Background work**: WorkManager handles cleanup/sync with periodic tasks.

