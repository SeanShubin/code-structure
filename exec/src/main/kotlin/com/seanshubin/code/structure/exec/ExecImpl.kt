package com.seanshubin.code.structure.exec

import java.io.IOException

class ExecImpl : Exec {
    override fun exec(vararg args: String) {
        val processBuilder = ProcessBuilder()
        processBuilder.command(*args)
        try {
            val process = processBuilder.start()
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                val command = args.joinToString(" ")
                throw RuntimeException("Unable to execute command $command, exited with code $exitCode")
            }
        } catch (ex: IOException) {
            val command = args.joinToString(" ")
            throw RuntimeException("Unable to execute command $command, exited by throwing exception", ex)
        }
    }
}
