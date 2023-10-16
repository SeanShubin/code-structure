package com.seanshubin.code.structure.domain

data class Arrows(val inCycle:Set<String>, val notInCycle:Set<String>){
    val all:Set<String> get() = inCycle + notInCycle
}
