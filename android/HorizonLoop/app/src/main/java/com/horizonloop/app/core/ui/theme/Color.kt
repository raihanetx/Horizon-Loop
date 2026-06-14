package com.horizonloop.app.core.ui.theme

import androidx.compose.ui.graphics.Color

// ── Unified palette ────────────────────────────────────────────────────────
// White-dominant / black-secondary theme. Backgrounds, cards and modals
// use white tones; text, icons and primary actions use black tones.
// The legacy names (Deep, Surface, Muted, Mid, Dark, Accent, AccentSoft)
// keep the same roles as before — only their values are inverted.

// Primary surfaces — pure white and black (no dark grays)
val Deep = Color(0xFFFFFFFF)        // pure white — app background (dominant)
val Surface = Color(0xFFFFFFFF)     // white — modals/sheets
val Muted = Color(0xFFF4F4F5)       // very light gray — elevated buttons/tiles
val Mid = Color(0xFF71717A)         // cool gray — secondary text & icons
val Dark = Color(0xFF000000)        // pure black — primary text (secondary)
val Accent = Color(0xFF000000)      // primary emphasis (black)
val AccentSoft = Color(0xFF52525B)  // dark gray — secondary emphasis

// Home page design tokens (light theme spec)
val AppBg = Color(0xFFFFFFFF)       // spec appBg — white
val CardBg = Color(0xFFF4F4F5)      // spec cardBg — soft light gray
val SurfLight = Color(0xFFE4E4E7)   // spec surfaceLight — light gray
val TextPri = Color(0xFF000000)     // spec textPrimary — black
val TextSec = Color(0xFF52525B)     // spec textSecondary
val TextMut = Color(0xFFA1A1AA)     // spec textMuted
val Brd = Color(0xFFE4E4E7)         // spec border — light gray

// Home page redesign palette — pure white and black
val HomeBg = Color(0xFFFFFFFF)      // white — page background
val HomeCard = Color(0xFFF4F4F5)    // soft gray — card surface
val HomeElevated = Color(0xFFFFFFFF)// white — elevated (icon tiles, chips)
val HomeAccent = Color(0xFF000000)  // black — primary action / accent
val HomeGradientStart = Color(0xFF52525B)
val HomeGradientEnd = Color(0xFF27272A)
val HomeTextPrimary = Color(0xFF000000)   // black — primary text
val HomeTextSecondary = Color(0xFF52525B) // dark gray — secondary text
val HomeTextTertiary = Color(0xFF71717A)  // mid gray — tertiary text
val HomeDivider = Color(0xFFE4E4E7)       // light gray — dividers
val HomeSubtitleNo = Color(0xFF71717A)

// Translucent black overlays (for tints on the white search bar, debug panel, etc.)
val Black6 = Color(0x0F000000)
val Black8 = Color(0x14000000)
val Black12 = Color(0x1F000000)
val Black15 = Color(0x26000000)
val Black20 = Color(0x33000000)

// Translucent white overlays (kept for backward compatibility — used as
// the inverse on dark surfaces; on a light theme most call sites swap
// to the Black* tokens above).
val White6 = Color(0x0FFFFFFF)
val White8 = Color(0x14FFFFFF)
val White12 = Color(0x1FFFFFFF)
val White15 = Color(0x26FFFFFF)
val White20 = Color(0x33FFFFFF)
