# UI Layer AGENTS.md

## Purpose
UI layer handles all Jetpack Compose screens, components, viewmodel, and theming.

## Ownership
Owner: HorizonLoop App Team
Scope: All files in `com.horizonloop.app.ui`

## Parent
App: [/tmp/horizon-loop/android/HorizonLoop/app/src/main/java/com/horizonloop/app/AGENTS.md](../AGENTS.md)

## Local Contracts
- All UI components must be Compose@Composable functions
- Screens should delegate to smaller component files
- ViewModel handles all business logic and state
- Theme should be consistent across all composables

## File Size Compliance
- All files in this directory must be ≤200 lines
- Current largest file: AppViewModel.kt (146 lines)
- All files currently comply with the 200-line rule ✓

## Work Guidance
- Components should be split by feature/responsibility
- Use naming pattern `Component_Purpose.kt` for split files
- ViewModel should use `mutableStateOf` for reactive state
- Screens should be minimal - delegate to components

## Verification
Run `./gradlew assembleDebug` - compilation verifies all UI files are valid.

## Child DOX Index
- [viewmodel/AGENTS.md](viewmodel/AGENTS.md) - ViewModels and business logic
- [components/AGENTS.md](components/AGENTS.md) - Reusable UI components
- [screens/AGENTS.md](screens/AGENTS.md) - Screen composables
- [theme/AGENTS.md](theme/AGENTS.md) - Colors, typography, icons
