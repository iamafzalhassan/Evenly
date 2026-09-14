# Evenly

Offline-first shared expense tracker for Android and iOS, built with Kotlin Multiplatform and Compose Multiplatform.

Log shared expenses with friends or flatmates, even without a connection, split them equally, by percentage or by exact amounts, and settle up with as few payments as possible.

## Status

In development.

## Tech stack

| Layer | Choice |
|---|---|
| Shared code | Kotlin Multiplatform, Coroutines, Flow |
| UI | Compose Multiplatform (Android and iOS) |
| Architecture | Unidirectional data flow: immutable state down, events up |

## Project structure

| Module | Contents |
|---|---|
| `shared` | All domain logic, data and UI, in `commonMain` |
| `androidApp` | Android entry point |
| `iosApp` | iOS entry point (Xcode project) |

## Running

- Android: `./gradlew :androidApp:assembleDebug`, or run `androidApp` from Android Studio.
- iOS: open `iosApp/iosApp.xcodeproj` in Xcode on macOS and run.
