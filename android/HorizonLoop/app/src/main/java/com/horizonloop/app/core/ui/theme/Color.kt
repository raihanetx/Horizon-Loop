package com.horizonloop.app.core.ui.theme

import androidx.compose.ui.graphics.Color

// ── Mono (white + black) palette ──────────────────────────────────────────────
// Clean, minimal, high-contrast monochrome. ONE color combination used across
// the entire app — no tints, no hue accents. Just white, black, and a few
// shades of gray for hierarchy.

// Primary surfaces
val Deep = Color(0xFFFFFFFF)      // pure white — main background
val Surface = Color(0xFFF9F9F9)   // very light gray — cards (subtle separation)
val Muted = Color(0xFFF4F4F5)     // light gray — buttons, icon backgrounds
val Mid = Color(0xFF71717A)       // medium gray — muted text & icons
val Dark = Color(0xFF0A0A0A)      // near-black — primary text & icons

// Black accent (no color, just deeper black for active/selected states)
val Accent = Color(0xFF000000)    // pure black — active highlight
val AccentSoft = Color(0xFF27272A) // very dark gray — secondary highlight

// Translucent black overlays (for tints on the white debug panel, search bar, etc.)
val Black15 = Color(0x26000000)
val Black6 = Color(0x0F000000)
val Black8 = Color(0x14000000)
val Black12 = Color(0x1F000000)
val Black20 = Color(0x33000000)

// Backwards-compat aliases (these used to be white overlays before the mono
// palette switch; the names are kept so existing call sites still compile).
val White15 = Black15
val White6 = Black6
val White8 = Black8
val White12 = Black12
val White20 = Black20
