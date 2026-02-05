package com.seanshubin.code.structure.commands

import com.seanshubin.code.structure.runtime.Environment

interface Command {
    val source: String
    val category: String
    val id: String
    fun execute(environment: Environment)
}
