package com.seanshubin.code.structure.domain

interface Command {
    val id:String
    fun execute(environment: Environment)
}
