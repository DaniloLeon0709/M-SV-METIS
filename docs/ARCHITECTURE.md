# VitaAlert Architecture

## Overview
VitaAlert follows Clean Architecture with MVVM for the presentation layer. The project is split into Gradle modules to keep boundaries clear and enforce dependency direction.

```
:app -> :designsystem, :ble, :auth, :data, :domain, :core, :reports
:data -> :domain, :core
:auth -> :domain, :core
:ble -> :domain, :core
:designsystem -> (no app/domain dependencies)
:domain -> (no Android dependencies)
:core -> (shared Android utilities)
```

## Layers

### Domain
- Pure Kotlin models and repository interfaces.
- No Android dependencies.

### Data
- Room for offline-first persistence.
- Repository implementations that map entities to domain models.

### Auth
- Runtime uses `InMemoryAuthRepository` for a backend-free experience.
- Unit tests use `RetrofitAuthRepository` with `MockWebServer` to validate JSON contracts.

### BLE
- GATT UUIDs for standard Heart Rate (0x180D/0x2A37 notify).
- Best-effort SpO2 service support (0x1822).
- Demo mode injects measurements into Room for a backend-free runtime.

### App
- Compose UI + MVVM.
- Foreground service keeps BLE monitoring active.
- WorkManager schedules background maintenance.

## Dependency Injection
Hilt provides module-scoped dependencies. Public APIs are documented with KDoc to keep contracts explicit.

## Design System
Material 3 theme with Bogotá palette:
- Primary: `#F9C642`
- Error: `#E53935`
- Light background: `#F7F7F8`
- Dark background: `#0B0B0C`

## Testing
- Auth module uses `MockWebServer` for `/auth/login` and `/auth/refresh` unit tests.
- CI runs `ktlint`, `detekt`, `test`, and `assembleDebug`.
