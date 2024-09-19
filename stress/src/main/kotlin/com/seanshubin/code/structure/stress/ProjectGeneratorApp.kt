package com.seanshubin.code.structure.stress

import java.nio.file.Paths
import kotlin.system.exitProcess

object ProjectGeneratorApp {
    @JvmStatic
    fun main(args: Array<String>) {
        val baseDirName = args.getOrNull(0) ?: syntax()
        val baseDir = Paths.get(baseDirName)
        ProjectGenerator.generateProject(baseDir)
    }

    private fun syntax(): Nothing {
        println("Syntax: specify base directory")
        println("Names must be in file named 'names.txt'")
        println("Relations must be in file named 'relations.txt'")
        exitProcess(1)
    }
}