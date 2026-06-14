package com.horizonloop.app.core.ui.theme

import androidx.compose.ui.graphics.Color

// ── Unified palette ────────────────────────────────────────────────────────
// Whole-app theme now mirrors the home page's purple/gradient look.
// The legacy names (Deep, Surface, Muted, Mid, Dark, Accent, AccentSoft)
// now resolve to the same values as the Home* tokens, so every existing
// usage in the player, settings, dialogues, notes, loops, … picks up
// the new palette automatically.

// Primary surfaces — values mirror the home page Home* palette so the
// whole app (player, settings, dialogues, notes, loops, …) keeps the
// same purple/gradient look as the home screen.
val Deep = Color(0xFF121212)      // app background (was #0F0F0F)
val Surface = Color(0xFF1E1E1E)   // cards / dialog background
val Muted = Color(0xFF2D2D2D)     // subtle backgrounds & buttons (was #2A2A2A)
val Mid = Color(0xFF9CA3AF)       // secondary text & icons (was #8A8A8A)
val Dark = Color.White            // primary text (was #F5F5F5)
val Accent = Color(0xFFA052D4)    // brand accent — purple (was emerald #10B981)
val AccentSoft = Color(0xFFC084E0) // lighter purple for secondary highlights

// Home page design tokens (matches web spec)
val AppBg = Color(0xFF1A1A1A)     // spec appBg
val CardBg = Color(0xFF212121)    // spec cardBg
val SurfLight = Color(0xFF333333) // spec surfaceLight
val TextPri = Color(0xFFD4D4D4)   // spec textPrimary
val TextSec = Color(0xFF8A8A8A)   // spec textSecondary (alias of Mid)
val TextMut = Color(0xFF5A5A5A)   // spec textMuted
val Brd = Color(0xFF2E2E2E)       // spec border

// Home page redesign palette (from github.com/raihanetx/ui "Home page ui")
val HomeBg = Color(0xFF121212)
val HomeCard = Color(0xFF1E1E1E)
val HomeElevated = Color(0xFF2D2D2D)
val HomeAccent = Color(0xFFA052D4)
val HomeGradientStart = Color(0xFF667EEA)
val HomeGradientEnd = Color(0xFF764BA2)
val HomeTextPrimary = Color.White
val HomeTextSecondary = Color(0xFF9CA3AF)
val HomeTextTertiary = Color(0xFF6B7280)
val HomeDivider = Color(0xFF4B5563)
val HomeSubtitleNo = Color(0xFF6B7280)

// Translucent white overlays (for tints on the dark search bar, debug panel, etc.)
val White6 = Color(0x0FFFFFFF)
val White8 = Color(0x14FFFFFF)
val White12 = Color(0x1FFFFFFF)
val White15 = Color(0x26FFFFFF)
val White20 = Color(0x33FFFFFF)
