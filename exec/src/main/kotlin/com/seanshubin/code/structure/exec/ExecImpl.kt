package com.seanshubin.code.structure.exec

class ExecImpl:Exec {
    override fun exec(vararg args: String) {
        val processBuilder = ProcessBuilder()
        processBuilder.command(*args)
        val process = processBuilder.start()
        val exitCode = process.waitFor()
        if(exitCode != 0){
            val command = args.joinToString(" ")
            throw RuntimeException("Unable to execute command $command, exited with code $exitCode")
        }
    }
}
