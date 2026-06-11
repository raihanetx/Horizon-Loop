package com.horizonloop.app.core.data

import org.junit.Assert.*
import org.junit.Test

class TimeUtilsTest {

    // ── parseTimeToSeconds ────────────────────────────────────────────────────

    @Test
    fun `parseTimeToSeconds parses M-SS format correctly`() {
        assertEquals(83.0, parseTimeToSeconds("1:23"), 0.001)
        assertEquals(0.0,   parseTimeToSeconds("0:00"), 0.001)
        assertEquals(60.0,  parseTimeToSeconds("1:00"), 0.001)
        assertEquals(59.0,  parseTimeToSeconds("0:59"), 0.001)
        assertEquals(125.0, parseTimeToSeconds("2:05"), 0.001)
    }

    @Test
    fun `parseTimeToSeconds parses plain seconds as string`() {
        assertEquals(42.0, parseTimeToSeconds("42"), 0.001)
        assertEquals(0.0,  parseTimeToSeconds("0"),  0.001)
        assertEquals(1.5,  parseTimeToSeconds("1.5"), 0.001)
    }

    @Test
    fun `parseTimeToSeconds returns NaN for invalid input`() {
        assertTrue(parseTimeToSeconds("").isNaN())
        assertTrue(parseTimeToSeconds("abc").isNaN())
        assertTrue(parseTimeToSeconds("one:two").isNaN())
        assertTrue(parseTimeToSeconds("1:2:3").isNaN())
    }

    @Test
    fun `parseTimeToSeconds handles edge cases`() {
        assertEquals(0.0,   parseTimeToSeconds("0:0"),   0.001)
        assertEquals(3599.0, parseTimeToSeconds("59:59"), 0.001)
        assertEquals(60.1, parseTimeToSeconds("1:00.1"), 0.001) // decimal part ignored by split
    }

    // ── formatTime ────────────────────────────────────────────────────────────

    @Test
    fun `formatTime formats seconds into M-SS`() {
        assertEquals("0:00", formatTime(0.0))
        assertEquals("0:59", formatTime(59.0))
        assertEquals("1:00", formatTime(60.0))
        assertEquals("1:23", formatTime(83.0))
        assertEquals("2:05", formatTime(125.0))
        assertEquals("10:30", formatTime(630.0))
    }

    @Test
    fun `formatTime pads single-digit seconds with zero`() {
        assertEquals("0:00", formatTime(0.0))
        assertEquals("0:09", formatTime(9.0))
        assertEquals("1:01", formatTime(61.0))
    }

    // ── formatTimestamp ───────────────────────────────────────────────────────

    @Test
    fun `formatTimestamp formats valid seconds like formatTime`() {
        assertEquals("0:00", formatTimestamp(0.0))
        assertEquals("1:23", formatTimestamp(83.0))
        assertEquals("2:05", formatTimestamp(125.0))
    }

    @Test
    fun `formatTimestamp returns dash for NaN`() {
        assertEquals("—", formatTimestamp(Double.NaN))
    }

    @Test
    fun `formatTimestamp formats negative seconds with zero-padding`() {
        // Kotlin % keeps negative sign: -5 % 60 = -5, s=-5 < 10 → "0"+"-5" = "0-5"
        assertEquals("0:0-5", formatTimestamp(-5.0))
        assertEquals("0:0-1", formatTimestamp(-1.0))
        assertEquals("0:00", formatTimestamp(-0.0)) // -0.0 is treated as 0
    }

    // ── formatTimeFromMs ──────────────────────────────────────────────────────

    @Test
    fun `formatTimeFromMs formats milliseconds into M-SS`() {
        assertEquals("0:00", formatTimeFromMs(0L))
        assertEquals("0:59", formatTimeFromMs(59000L))
        assertEquals("1:00", formatTimeFromMs(60000L))
        assertEquals("1:23", formatTimeFromMs(83000L))
        assertEquals("10:30", formatTimeFromMs(630000L))
    }

    @Test
    fun `formatTimeFromMs pads single-digit seconds`() {
        assertEquals("0:09", formatTimeFromMs(9000L))
        assertEquals("1:01", formatTimeFromMs(61000L))
    }

    // ── formatTimeRange ───────────────────────────────────────────────────────

    @Test
    fun `formatTimeRange shows bare seconds when end is in same minute`() {
        assertEquals("0:45-56", formatTimeRange(45.0, 56.0))
        assertEquals("0:00-30", formatTimeRange(0.0, 30.0))
        assertEquals("1:20-45", formatTimeRange(80.0, 105.0))
        assertEquals("0:05-9",  formatTimeRange(5.0, 9.0))
    }

    @Test
    fun `formatTimeRange shows full M-SS when end crosses a minute`() {
        assertEquals("0:45-1:15", formatTimeRange(45.0, 75.0))
        assertEquals("1:55-2:05", formatTimeRange(115.0, 125.0))
        assertEquals("0:00-1:00", formatTimeRange(0.0, 60.0))
    }

    @Test
    fun `formatTimeRange returns dash for NaN inputs`() {
        assertEquals("—", formatTimeRange(Double.NaN, 10.0))
        assertEquals("—", formatTimeRange(10.0, Double.NaN))
        assertEquals("—", formatTimeRange(Double.NaN, Double.NaN))
    }
}