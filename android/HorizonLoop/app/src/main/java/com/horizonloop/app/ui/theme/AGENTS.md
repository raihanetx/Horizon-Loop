# Theme AGENTS.md

## Purpose
Theme configuration for Jetpack Compose: colors, typography, and icons.

## Ownership
Owner: HorizonLoop App Team
Scope: All files in `com.horizonloop.app.ui.theme`

## Parent
UI Layer: [/tmp/horizon-loop/android/HorizonLoop/app/src/main/java/com/horizonloop/app/ui/AGENTS.md](../AGENTS.md)

## Local Contracts
- Color.kt defines the app color palette
- Type.kt defines typography styles
- Icons.kt provides custom icon definitions
- Theme.kt composes everything into a MaterialTheme

## File Size Compliance (Verified)
- Icons.kt: 81 lines ✓
- Theme.kt: ~40 lines ✓
- Color.kt: ~40 lines ✓
- Type.kt: 44 lines ✓
- All theme files comply with 200-line rule ✓

## Work Guidance
- Use semantic color names (Dark, Deep, Mid, Surface, etc.)
- Typography should follow Material Design type scale
- Icons should be defined as Compose ImageVectors
- Theme should support both light and dark modes

## Verification
Run `./gradlew assembleDebug` - compilation verifies all theme files are valid.

## Child DOX Index
```
Color.kt - Color palette definitions
Type.kt - Typography styles
Icons.kt - Custom icon definitions
Theme.kt - MaterialTheme composition
```
