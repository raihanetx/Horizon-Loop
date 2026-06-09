# App AGENTS.md

## Purpose
Root AGENTS.md for the Android app package.

## Ownership
Owner: HorizonLoop App Team
Scope: All files in `com.horizonloop.app`

## Parent
Project Root: [/tmp/horizon-loop/AGENTS.md](../../../../../../AGENTS.md)

## Local Contracts
- MainActivity.kt is the app entry point
- MainViewModel wraps AppViewModel for activity use
- MainActivity_Content.kt contains main UI composition
- All UI state flows through ViewModels

## File Size Compliance
- MainActivity.kt: 55 lines ✓
- MainActivity_Content.kt: 118 lines ✓
- MainViewModel.kt: 95 lines ✓
- All files comply with 200-line rule ✓

## Work Guidance
- Entry point is MainActivity
- UI composed in MainActivityContent
- ViewModel pattern for state management

## Verification
Run `./gradlew assembleDebug` - compilation verifies all files are valid.

## Child DOX Index
```
data/ - Data layer: models, storage, API clients
ui/ - UI layer: screens, components, viewmodel, theme
utils/ - Utility functions
```
