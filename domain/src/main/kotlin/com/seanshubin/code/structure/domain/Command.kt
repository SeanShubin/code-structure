package com.seanshubin.code.structure.domain

interface Command {
    val category: String
    val id: String
    fun execute(environment: Environment)
}
