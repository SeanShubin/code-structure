package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.durationformat.DurationFormat
import java.time.Clock
import java.time.Duration

class Runner(
    private val args: Array<String>,
    private val clock: Clock,
    private val emitLine:(String)->Unit
) : Runnable {
    override fun run() {
        val startTime = clock.instant()
        emitLine("Started at $startTime")
        val pluralizedArgsSize = if(args.size == 1) "argument" else "arguments"
        emitLine("${args.size} command line $pluralizedArgsSize")
        args.forEachIndexed{ index, argument ->
            emitLine("  arg[$index] = '$argument'")
        }
        val endTime = clock.instant()
        val duration = Duration.between(startTime, endTime)
        val durationMilliseconds = duration.toMillis()
        emitLine("Ended at $endTime")
        emitLine(DurationFormat.milliseconds.format(durationMilliseconds))
    }
}
