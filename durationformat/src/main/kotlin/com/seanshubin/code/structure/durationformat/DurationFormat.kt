package com.seanshubin.code.structure.durationformat

class DurationFormat(private val scales: List<Scale>, val padded: Boolean) {
    fun parse(s: String): Long {
        val whitespacePattern = Regex("\\s+")
        val parts = s.trim().split(whitespacePattern)
        val chunked = parts.chunked(2)
        val numbers = chunked.map(::chunkToNumber)
        val sum = numbers.sum()
        return sum
    }

    fun format(quantity: Int): String = format(quantity.toLong())

    fun format(quantity: Long): String {
        val initial = Builder(quantity, emptyList())
        val builder = scales.fold(initial, ::appendSegment)
        val segments = if (padded) filterSegmentsPadded(builder.segments) else filterSegments(builder.segments)
        return if (segments.isEmpty()) segmentToString(builder.segments.first())
        else segments.map(::segmentToString).reversed().joinToString(" ")
    }

    fun normalize(s: String): String {
        val quantity = parse(s)
        val formatted = format(quantity)
        return formatted
    }

    private fun filterSegments(segments: List<Segment>): List<Segment> {
        return if (segments.size == 1) segments
        else segments.filter { segment -> segment.quantity != 0L }
    }

    private fun filterSegmentsPadded(segments: List<Segment>): List<Segment> {
        return if (segments.size == 1) segments
        else segments.reversed().dropWhile { segment -> segment.quantity == 0L }.reversed()
    }

    private fun segmentToString(segment: Segment): String =
        if (padded) segment.toPaddedString()
        else segment.toString()

    private fun chunkToNumber(chunk: List<String>): Long {
        return when {
            chunk.size == 1 -> toLong(chunk[0])
            chunk.size == 2 -> scaledToNumber(toLong(chunk[0]), chunk[1])
            else -> throw RuntimeException("Expected 1 or 2 parts, got ${chunk.size}")
        }
    }

    private fun scaledToNumber(quantity: Long, scaleName: String): Long {
        val scale = fromScaleName(scaleName)
        var value = quantity
        var scaleIndex = 0
        while (scale != scales[scaleIndex]) {
            value *= scales[scaleIndex].quantity
            scaleIndex++
        }
        return value
    }

    private fun fromScaleName(name: String): Scale {
        val scale = scales.find { it.matches(name) }
        return scale ?: throw RuntimeException(composeScaleNotFoundMessage(name))
    }

    private fun composeScaleNotFoundMessage(name: String): String {
        val scalesString = scales.map { it.plural }.joinToString(", ")
        return "'$name' does not match a valid time unit ($scalesString)"
    }

    private fun toLong(s: String): Long = try {
        s.toLong()
    } catch (ex: NumberFormatException) {
        throw RuntimeException("Unable to convert '$s' to a number")
    }

    data class Scale(val quantity: Long, val singular: String, val plural: String, val padSize: Int) {
        fun name(x: Long): String = if (x == 1L) singular else plural
        fun paddedName(x: Long): String {
            val maxSize = maxOf(singular.length, plural.length)
            val name = name(x)
            val paddingNeeded = maxSize - name.length
            val padding = " ".repeat(paddingNeeded)
            return name + padding
        }

        fun matches(s: String): Boolean =
            singular.equals(s, ignoreCase = true) || plural.equals(s, ignoreCase = true)
    }

    companion object {
        private val nanosecondScale = Scale(1000, "nanosecond", "nanoseconds", 3)
        private val microsecondScale = Scale(1000, "microsecond", "microseconds", 3)
        private val millisecondScale = Scale(1000, "millisecond", "milliseconds", 3)
        private val secondScale = Scale(60, "second", "seconds", 2)
        private val minuteScale = Scale(60, "minute", "minutes", 2)
        private val hourScale = Scale(24, "hour", "hours", 2)
        private val dayScale = Scale(Long.MAX_VALUE, "day", "days", 0)
        private val secondScales = listOf(
            secondScale,
            minuteScale,
            hourScale,
            dayScale
        )
        private val millisecondScales = listOf(millisecondScale) + secondScales
        private val nanosecondScales =
            listOf(nanosecondScale, microsecondScale) + millisecondScales

        private data class Segment(val scale: Scale, val quantity: Long) {
            override fun toString(): String = "$quantity ${scale.name(quantity)}"
            fun toPaddedString(): String = "${pad(quantity)} ${scale.paddedName(quantity)}"
            fun pad(x: Long): String = pad(x.toString())
            fun pad(s: String): String {
                val paddingNeeded = scale.padSize - s.length
                return if (paddingNeeded > 0) " ".repeat(scale.padSize - s.length) + s else s
            }
        }

        private data class Builder(val remainValue: Long, val segments: List<Segment>)

        private fun appendSegment(builder: Builder, scale: Scale): Builder {
            val current = builder.remainValue % scale.quantity
            val remain = builder.remainValue / scale.quantity
            val segment = Segment(scale, current)
            return Builder(remain, builder.segments + segment)
        }

        val seconds = DurationFormat(secondScales, padded = false)
        val secondsPadded = DurationFormat(secondScales, padded = true)
        val milliseconds = DurationFormat(millisecondScales, padded = false)
        val millisecondsPadded = DurationFormat(millisecondScales, padded = true)
        val nanoseconds = DurationFormat(nanosecondScales, padded = false)
        val nanosecondsPadded = DurationFormat(nanosecondScales, padded = true)
    }
}
