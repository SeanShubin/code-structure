package com.seanshubin.code.structure.console

import com.seanshubin.code.structure.domain.Runner
import java.time.Clock

class Dependencies(args:Array<String>) {
    private val clock:Clock = Clock.systemUTC()
    private val emitLine:(String)->Unit = ::println
    val runner:Runnable = Runner(
        args, clock, emitLine
    )
}
