package com.horizonloop.app.core.ui.theme

import androidx.compose.ui.graphics.Color

// ── Dark + Emerald (60-30-10) palette ────────────────────────────────────────
// 60% primary (background), 30% secondary (cards), 10% accent (brand).
// Easy on the eyes, professional, dark mode by default.

// Primary surfaces
val Deep = Color(0xFF0F0F0F)      // near-black — 60% main background
val Surface = Color(0xFF1E1E1E)   // dark gray — 30% cards
val Muted = Color(0xFF2A2A2A)     // slightly lighter dark — subtle backgrounds & buttons
val Mid = Color(0xFF8A8A8A)       // cool gray — muted text & icons
val Dark = Color(0xFFF5F5F5)      // off-white — primary text

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

// Emerald green — 10% brand accent
val Accent = Color(0xFF10B981)    // emerald-500 — primary brand highlight
val AccentSoft = Color(0xFF34D399) // emerald-400 — secondary brand highlight

// Translucent white overlays (for tints on the dark search bar, debug panel, etc.)
val White6 = Color(0x0FFFFFFF)
val White8 = Color(0x14FFFFFF)
val White12 = Color(0x1FFFFFFF)
val White15 = Color(0x26FFFFFF)
val White20 = Color(0x33FFFFFF)
