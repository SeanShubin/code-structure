package com.seanshubin.code.structure.stress

import java.nio.file.Paths
import java.time.Clock
import java.time.Duration
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
        val clock = Clock.systemUTC()
        val start = clock.instant()
        if (args.size != 4) syntax()
        if (args[0] != "depth") syntax()
        if (args[2] != "breadth") syntax()
        val depth = args[1].toIntOrNull() ?: syntax()
        val breadth = args[3].toIntOrNull() ?: syntax()
        val seed = 12345L
        val random: Random = Random(seed)
        val baseDir = Paths.get("generated", "stress-test-project")
        val relationsPerName = 10
        val generator = Generator(depth, breadth, natoPhonetic, random)
        val names = generator.createNames()
        val relations = generator.createRelations(names, relationsPerName)
        val compilationUnits = generator.compilationUnits(names, relations)
        val homeDir = System.getenv("HOME")
        val persistence = Persistence(baseDir, homeDir, depth, breadth)
        persistence.store(names, relations, compilationUnits)
        val end = clock.instant()
        persistence.createReadme(start, end)
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
/*

13m39.948s ./scripts/stress.sh depth 3 breadth 6
 0m10.878s ./scripts/stress.sh depth 4 breadth 2
 5m15.982s ./scripts/stress.sh depth 4 breadth 3
175m9.447s ./scripts/stress.sh depth 4 breadth 4
           Took 2 hours 55 minutes 4 seconds 826 milliseconds
 */
