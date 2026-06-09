# ViewModel AGENTS.md

## Purpose
ViewModels handle business logic, state management, and communication between UI and data layers.

## Ownership
Owner: HorizonLoop App Team
Scope: All files in `com.horizonloop.app.ui.viewmodel`

## Parent
UI Layer: [/tmp/horizon-loop/android/HorizonLoop/app/src/main/java/com/horizonloop/app/ui/AGENTS.md](../AGENTS.md)

## Local Contracts
- AppViewModel is the main state holder for the app
- Translation logic is separated into AppViewModel_Translation
- Helper utilities are in AppViewModel_Helpers
- MainViewModel wraps AppViewModel for MainActivity

## File Size Compliance (Verified)
- AppViewModel.kt: 146 lines ✓
- AppViewModel_Translation.kt: 64 lines ✓
- AppViewModel_Helpers.kt: 65 lines ✓
- MainViewModel.kt: 95 lines ✓
- All files comply with 200-line rule ✓

## Work Guidance
- Keep AppViewModel as the central state management point
- Extract logical groups (translation, helpers) to separate files
- Use `viewModelScope.launch` for coroutine operations
- State properties should use `by mutableStateOf` for reactivity

## Verification
Run `./gradlew assembleDebug` - compilation verifies all viewmodel files are valid.

## Child DOX Index
```
AppViewModel.kt - Main state management
AppViewModel_Translation.kt - Translation/transcription logic
AppViewModel_Helpers.kt - Utility functions
MainViewModel.kt - MainActivity ViewModel wrapper
```
