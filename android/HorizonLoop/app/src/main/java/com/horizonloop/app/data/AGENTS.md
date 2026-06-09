# Data Layer AGENTS.md

## Purpose
Data layer handles all domain models, persistence, API communication, and media file scanning.

## Ownership
Owner: HorizonLoop App Team
Scope: All files in `com.horizonloop.app.data`

## Parent
Root: [/tmp/horizon-loop/AGENTS.md](../../../../../AGENTS.md)

## Local Contracts
- All data classes must be defined in `Models.kt`
- API services should follow single responsibility
- Media scanning must be non-blocking (use coroutines)
- Storage operations must handle permissions properly

## File Size Compliance
- All files in this directory must be ≤200 lines
- Current largest file: GroqApiService.kt (82 lines)
- All 10 data files currently comply with the 200-line rule ✓

## Work Guidance
- Use `Audio`, `Dialogue`, `Loop`, `Note` data classes from Models.kt
- Prefer `suspend` functions for I/O operations
- MediaScanner should use `Dispatchers.IO` for background work
- API clients should use OkHttp with proper timeouts

## Verification
Run `./gradlew assembleDebug` - compilation verifies all data layer files are valid.

## Child DOX Index
```
Models.kt - Domain data classes (63 lines)
AudioData.kt - Audio file data structure
ApiKeyStorage.kt - Secure API key storage (54 lines)
DialogueStorage.kt - Dialogue persistence (56 lines)
GroqApiService.kt - Groq API communication (82 lines)
GroqClient.kt - HTTP client for Groq
VideoScanner_Main.kt - MediaStore video scanner (53 lines)
AudioExtractor_Main.kt - Audio extraction from video (54 lines)
AudioExtractor_WavWriter.kt - WAV file writing
MediaPlaybackManager_Main.kt - Media playback control (65 lines)
```
