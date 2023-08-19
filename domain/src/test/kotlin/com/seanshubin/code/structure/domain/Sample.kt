package com.seanshubin.code.structure.domain

import java.nio.file.Path
import java.nio.file.Paths

class Sample {
    var index: Int = 0
    fun analysis(): Analysis = Analysis(observations())

    fun observations(): Observations = Observations(paths())

    fun paths(howMany: Int = 3,
              dirPrefix:String="dir-",
              dirSuffix:String="",
              filePrefix:String="file-",
              fileSuffix:String=""): List<Path> = (1..howMany).map {
        path(dirPrefix, dirSuffix, filePrefix, fileSuffix)
    }

    fun path(dirPrefix:String="dir-",
             dirSuffix:String="",
             filePrefix:String="file-",
             fileSuffix:String=""): Path = Paths.get(pathName(dirPrefix, dirSuffix, filePrefix, fileSuffix))

    fun dir(prefix:String = "dir-", suffix:String=""): Path = Paths.get(dirName(prefix, suffix))

    fun pathName(
        dirPrefix:String="dir-",
        dirSuffix:String="",
        filePrefix:String="file-",
        fileSuffix:String=""): String = "${dirName(dirPrefix, dirSuffix)}/${fileName(filePrefix, fileSuffix)}"

    fun fileName(prefix:String = "file-", suffix:String = ""): String = string("file-", suffix)

    fun dirName(prefix:String = "dir-", suffix:String=""): String = string("dir-", suffix)

    fun string(prefix: String, suffix:String): String = "$prefix${++index}$suffix"
}
