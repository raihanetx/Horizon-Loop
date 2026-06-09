# Utils AGENTS.md

## Purpose
Utility classes and helper functions.

## Ownership
Owner: HorizonLoop App Team
Scope: All files in `com.horizonloop.app.utils`

## Parent
App: [/tmp/horizon-loop/android/HorizonLoop/app/src/main/java/com/horizonloop/app/AGENTS.md](../AGENTS.md)

## Local Contracts
- Utilities should be stateless helper functions
- Common formatting and conversion logic goes here

## File Size Compliance (Verified)
- TimeUtils.kt: 41 lines ✓
- Complies with 200-line rule ✓

## Work Guidance
- Keep utilities focused on a single domain (e.g., time formatting)
- Use extension functions where appropriate
- Avoid dependencies on Android components when possible

## Verification
Run `./gradlew assembleDebug` - compilation verifies all utility files are valid.

## Child DOX Index
```
TimeUtils.kt - Time formatting utilities
```
