package com.seanshubin.code.structure.domain

interface Command {
    fun execute(environment: Environment)
}
