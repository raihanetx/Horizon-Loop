# Components AGENTS.md

## Purpose
Reusable UI components for the HorizonLoop app.

## Ownership
Owner: HorizonLoop App Team
Scope: All files in `com.horizonloop.app.ui.components`

## Parent
UI Layer: [/tmp/horizon-loop/android/HorizonLoop/app/src/main/java/com/horizonloop/app/ui/AGENTS.md](../AGENTS.md)

## Local Contracts
- Components should be small, focused, single-responsibility
- Naming pattern: `Category_Purpose.kt`
- Components receive data and callbacks, not direct ViewModel access

## File Size Compliance (Verified)
- AudioComponents.kt: 95 lines ✓
- AudioControls_Main.kt: 91 lines ✓
- LoopComponents_AddDialog.kt: 87 lines ✓
- LoopComponents_Card.kt: 82 lines ✓
- LoopComponents_AddDialog_Form.kt: 82 lines ✓
- SpeedComponents.kt: 80 lines ✓
- NoteComponents_Tab.kt: 76 lines ✓
- NoteComponents_Card.kt: 75 lines ✓
- TranslationDebugPanel_Step.kt: 74 lines ✓
- NoteComponents_Dialog_Add.kt: 67 lines ✓
- CapsuleMenu_Main.kt: 58 lines ✓
- LoopComponents_Tab.kt: 53 lines ✓
- TranslationDebugPanel_Main.kt: 54 lines ✓
- LoopComponents_Dialogs.kt: 43 lines ✓
- CleanComponents_Display.kt: 43 lines ✓
- CapsuleMenu_Row.kt: 42 lines ✓
- All 35 component files comply with 200-line rule ✓

## Work Guidance
- Split large components into logical sub-components
- Use `_` suffix for helper/internal files (e.g., `Dialog_Form`)
- Components should be pure UI - no business logic
- Dialog components should handle their own state

## Verification
Run `./gradlew assembleDebug` - compilation verifies all component files are valid.

## Child DOX Index
```
AudioControls_* - Audio playback controls (Main, Progress)
CapsuleMenu_* - Capsule menu (Main, Row)
CleanComponents_* - Clean tab display (Main, Display)
DialogueComponents_* - Dialogue cards and tabs (Card, Tab)
LoopComponents_* - Loop management (Card, Tab, Dialogs, AddDialog, Helpers)
NoteComponents_* - Note management (Card, Tab, Dialogs)
SpeedComponents.kt - Speed selector
TranslationDebugPanel_* - Debug panel (Main, Header, Step)
```
