# Screens AGENTS.md

## Purpose
Screen composables that serve as the main views in the app.

## Ownership
Owner: HorizonLoop App Team
Scope: All files in `com.horizonloop.app.ui.screens`

## Parent
UI Layer: [/tmp/horizon-loop/android/HorizonLoop/app/src/main/java/com/horizonloop/app/ui/AGENTS.md](../AGENTS.md)

## Local Contracts
- Screens are top-level composables that compose components
- Each screen should have a clear purpose (Home, Player, Settings)
- Screens receive ViewModel or state via parameters

## File Size Compliance (Verified)
- PlayerScreen_Main.kt: 75 lines ✓
- HomeScreen_Settings.kt: 60 lines ✓
- SettingsDialog_Main.kt: 54 lines ✓
- HomeScreen_Filters.kt: 53 lines ✓
- HomeScreen_Main.kt: 48 lines ✓
- HomeScreen_Search.kt: 44 lines ✓
- PlayerScreen_TabContent.kt: 30 lines ✓
- All screen files comply with 200-line rule ✓

## Work Guidance
- Screens should be thin - delegate to components
- Keep screen-level state minimal
- Use consistent parameter naming across screens
- Pass callbacks for all user actions

## Verification
Run `./gradlew assembleDebug` - compilation verifies all screen files are valid.

## Child DOX Index
```
HomeScreen_* - Home screen (Main, Search, Settings, Filters)
PlayerScreen_* - Player screen (Main, TabContent)
SettingsDialog_* - Settings dialog (Main)
```
