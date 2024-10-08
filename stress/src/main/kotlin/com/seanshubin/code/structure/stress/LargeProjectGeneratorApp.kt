package com.seanshubin.code.structure.stress

import java.nio.file.Paths
import kotlin.random.Random
import kotlin.system.exitProcess

object LargeProjectGeneratorApp {
    @JvmStatic
    fun main(args: Array<String>) {
        run(args)
    }

    fun syntax(): Nothing {
        println("Pass 4 arguments in the following form")
        println("depth <number> breadth <number>")
        println("For example, if you want a depth of 3 and breadth of 4, use")
        println("depth 3 breadth 4")
        exitProcess(1)
    }

    fun run(args: Array<String>) {
        if (args.size != 4) syntax()
        if (args[0] != "depth") syntax()
        if (args[2] != "breadth") syntax()
        val depth = args[1].toIntOrNull() ?: syntax()
        val breadth = args[3].toIntOrNull() ?: syntax()
        val seed = 12345L
        val random = Random(seed)
        val baseDir = Paths.get("generated", "stress-test-project")
        val projectDir = baseDir.resolve("depth-$depth-breadth-$breadth")

        val relationsPerName = 10
        val generator = LargeProjectGenerator(depth, breadth, natoPhonetic, random)
        val names = generator.createNames()
        val relations = generator.createRelations(names, relationsPerName)
        ProjectGenerator.writeNames(projectDir, names)
        ProjectGenerator.writeRelations(projectDir, relations)
        ProjectGenerator.generateProject(projectDir)
    }

    val natoPhonetic = listOf(
        "Alfa",
        "Bravo",
        "Charlie",
        "Delta",
        "Echo",
        "Foxtrot",
        "Golf",
        "Hotel",
        "India",
        "Juliett",
        "Kilo",
        "Lima",
        "Mike",
        "November",
        "Oscar",
        "Papa",
        "Quebec",
        "Romeo",
        "Sierra",
        "Tango",
        "Uniform",
        "Victor",
        "Whiskey",
        "X-ray",
        "Yankee",
        "Zulu"
    )
}
