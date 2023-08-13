package com.seanshubin.code.structure.console

object EntryPoint {
    @JvmStatic
    fun main(args: Array<String>) {
        Dependencies(args).runner.run()
    }
}