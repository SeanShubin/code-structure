package com.seanshubin.code.structure.domain

interface CommandRunner {
    fun execute(command: Command)
}
