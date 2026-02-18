package com.seanshubin.code.structure.duration.format

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

//
// This file was imported from: ../kotlin-reusable
// Module: duration-format
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

class DurationFormatTest {
    @Test
    fun format() {
        verify("0 milliseconds", milliseconds(0))
        verify("1 millisecond", milliseconds(1))
        verify("999 milliseconds", milliseconds(999))
        verify("1 second", seconds(1))
        verify("1 second 1 millisecond", seconds(1) + milliseconds(1))
        verify("1 second 2 milliseconds", seconds(1) + milliseconds(2))
        verify("2 seconds 1 millisecond", seconds(2) + milliseconds(1))
        verify("2 seconds 2 milliseconds", seconds(2) + milliseconds(2))
        verify("1 minute", minutes(1))
        verify("2 minutes", minutes(2))
        verify("1 hour", hours(1))
        verify("2 hours", hours(2))
        verify("1 day", days(1))
        verify("2 days", days(2))
        verify("106751991167 days 7 hours 12 minutes 55 seconds 807 milliseconds", Long.MAX_VALUE)
    }

    @Test
    fun parse() {
        assertParse("0", 0L)
        assertParse("100", 100L)
        assertParse("1 day", 86_400_000L)
        assertParse("5 seconds", 5_000L)
        assertParse("2 days", 172_800_000L)
        assertParse("5 minutes", 300_000L)
        assertParse("10 hours", 36_000_000L)
        assertParse("1 second", 1_000L)
        assertParse("1 millisecond", 1L)
        assertParse("500 milliseconds", 500L)
        assertParse("55 minutes", 3_300_000L)
        assertParse("22", 22L)
        assertParse("1 day 5 hours 2 minutes 1 second 123 milliseconds", 104_521_123L)
        assertParse("2 Days 1 Hour 1 Minute 53 Seconds 1 Millisecond", 176_513_001L)
        assertParse("32 days 5 hours", 2_782_800_000L)
        assertParse("1 day 2 hours 1 day", 180_000_000L)
        assertParse("1 hour 2 days 1 hours", 180_000_000L)
        assertParse("25 days", 2_160_000_000L)
        assertParse("9223372036854775807", 9_223_372_036_854_775_807L)
        assertParse("9223372036854775807 milliseconds", 9_223_372_036_854_775_807L)
        assertParse("106751991167 days 7 hours 12 minutes 55 seconds 807 milliseconds", 9_223_372_036_854_775_807L)
    }

    @Test
    fun parseTrimsWhitespace() {
        assertEquals(60_000L, DurationFormat.milliseconds.parse(" 1  minute "))
    }

    @Test
    fun backAndForth() {
        assertBackAndForth("1 day 10 hours 17 minutes 36 seconds 789 milliseconds")
        assertBackAndForth("1 day 10 hours 36 seconds 789 milliseconds")
        assertBackAndForth("10 hours 17 minutes 36 seconds 789 milliseconds")
        assertBackAndForth("1 day 10 hours 17 minutes 36 seconds")
        assertBackAndForth("17 minutes")
        assertBackAndForth("789 milliseconds")
        assertBackAndForth("1 day 5 hours 2 minutes 1 second 123 milliseconds")
        assertBackAndForth("2 days 1 hour 1 minute 53 seconds 1 millisecond")
        assertBackAndForth("25 days")
        assertBackAndForth("0 milliseconds")
    }

    @Test
    fun errorMessage() {
        assertErrorMessage(
            "1 foo",
            """'foo' does not match a valid time unit (milliseconds, seconds, minutes, hours, days)"""
        )
        assertErrorMessage(
            "1 SecondsA",
            """'SecondsA' does not match a valid time unit (milliseconds, seconds, minutes, hours, days)"""
        )
        assertErrorMessage(
            "a 1 foo",
            """Unable to convert 'a' to a number"""
        )
        assertErrorMessage(
            "1 foo 3",
            """'foo' does not match a valid time unit (milliseconds, seconds, minutes, hours, days)"""
        )
        assertErrorMessage(
            "seconds",
            """Unable to convert 'seconds' to a number"""
        )
        assertErrorMessage(
            "1 foo 2 bar",
            """'foo' does not match a valid time unit (milliseconds, seconds, minutes, hours, days)"""
        )
    }

    @Test
    fun orderShouldNotMatterForParsing() {
        assertEquals(12_034L, DurationFormat.milliseconds.parse("12 seconds 34 milliseconds"))
        assertEquals(12_034L, DurationFormat.milliseconds.parse("34 milliseconds 12 seconds"))
    }

    @Test
    fun duplicatesGetAddedTogether() {
        assertEquals(14_034L, DurationFormat.milliseconds.parse("12 seconds 34 milliseconds 2 seconds"))
    }

    @Test
    fun padded() {
        val normalize: (String) -> String = DurationFormat.millisecondsPadded::normalize
        assertEquals("  0 milliseconds", normalize("0 milliseconds"))
        assertEquals("  1 millisecond ", normalize("1 millisecond"))
        assertEquals("  2 milliseconds", normalize("2 milliseconds"))
        assertEquals("999 milliseconds", normalize("999 milliseconds"))
        assertEquals(" 1 second    0 milliseconds", normalize("1 second"))
        assertEquals(" 1 second    1 millisecond ", normalize("1 second 1 millisecond"))
        assertEquals(" 1 second    2 milliseconds", normalize("1 second 2 milliseconds"))
        assertEquals(" 1 second  999 milliseconds", normalize("1 second 999 milliseconds"))
        assertEquals(" 2 seconds   0 milliseconds", normalize("2 seconds"))
        assertEquals(" 2 seconds   1 millisecond ", normalize("2 seconds 1 millisecond"))
        assertEquals(" 2 seconds   2 milliseconds", normalize("2 seconds 2 milliseconds"))
        assertEquals(" 2 seconds 999 milliseconds", normalize("2 seconds 999 milliseconds"))
        assertEquals("59 seconds   0 milliseconds", normalize("59 seconds"))
        assertEquals("59 seconds   1 millisecond ", normalize("59 seconds 1 millisecond"))
        assertEquals("59 seconds   2 milliseconds", normalize("59 seconds 2 milliseconds"))
        assertEquals("59 seconds 999 milliseconds", normalize("59 seconds 999 milliseconds"))

        assertEquals("1 day   0 hours  0 minutes  0 seconds   0 milliseconds", normalize("1 day"))
        assertEquals(
            "1 day   1 hour   1 minute   1 second    1 millisecond ",
            normalize("1 day 1 hour 1 minute 1 second 1 millisecond")
        )
        assertEquals(
            "2 days  2 hours  2 minutes  2 seconds   2 milliseconds",
            normalize("2 days 2 hours 2 minutes 2 seconds 2 milliseconds")
        )
        assertEquals(
            "2 days 23 hours 59 minutes 59 seconds 999 milliseconds",
            normalize("2 days 23 hours 59 minutes 59 seconds 999 milliseconds")
        )
    }

    private fun assertParse(verbose: String, expected: Long) {
        val actual = DurationFormat.milliseconds.parse(verbose)
        assertEquals(expected, actual)
    }

    private fun assertBackAndForth(verbose: String) {
        val parsed: Long = DurationFormat.milliseconds.parse(verbose)
        val formatted = DurationFormat.milliseconds.format(parsed)
        assertEquals(verbose, formatted)
    }

    private fun assertErrorMessage(verbose: String, expected: String) {
        try {
            DurationFormat.milliseconds.parse(verbose)
            fail("Expected '$verbose' to throw an exception during parsing")
        } catch (ex: Exception) {
            assertEquals(expected, ex.message)
        }
    }

    private fun verify(expected: String, milliseconds: Long) {
        val actual = DurationFormat.milliseconds.format(milliseconds)
        assertEquals(expected, actual, milliseconds.toString())
    }

    private fun milliseconds(x: Long): Long = x
    private fun seconds(x: Long): Long = milliseconds(x * 1000)
    private fun minutes(x: Long): Long = seconds(x * 60)
    private fun hours(x: Long): Long = minutes(x * 60)
    private fun days(x: Long): Long = hours(x * 24)
}
