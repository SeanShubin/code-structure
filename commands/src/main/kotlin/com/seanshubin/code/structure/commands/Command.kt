package com.seanshubin.code.structure.commands

import com.seanshubin.code.structure.appconfig.Environment

interface Command {
    val source: String
    val category: String
    val id: String
    fun execute(environment: Environment)
}
