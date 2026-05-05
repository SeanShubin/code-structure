package com.seanshubin.code.structure.fileselection

//
// This file was imported from: ../kotlin-reusable
// Module: file-selection
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

import com.seanshubin.code.structure.contract.delegate.FilesContract
import java.nio.file.FileVisitResult
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

class FileChooserImpl(
    private val files: FilesContract,
    private val notify: FileSelectionNotify? = null
) : FileChooser {
    override fun choose(fileSelection: FileSelection): List<Path> {
        val baseDir = fileSelection.baseDir
        val includePatterns = fileSelection.includePatterns.map { Regex(it) to it }
        val excludePatterns = fileSelection.excludePatterns.map { Regex(it) to it }
        val skipDirPatterns = fileSelection.skipDirectoryPatterns.map { Regex(it) to it }
        val results = mutableListOf<Path>()

        files.walkFileTree(baseDir, object : SimpleFileVisitor<Path>() {
            override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
                if (dir == baseDir) return FileVisitResult.CONTINUE
                val relative = normalizedRelative(baseDir, dir)
                val matched = skipDirPatterns.firstOrNull { (regex, _) -> regex.matches(relative) }
                return if (matched != null) {
                    notify?.onSkipDirectory(relative, matched.second)
                    FileVisitResult.SKIP_SUBTREE
                } else {
                    FileVisitResult.CONTINUE
                }
            }

            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                val relative = normalizedRelative(baseDir, file)
                val matchedInclude = includePatterns.firstOrNull { (regex, _) -> regex.matches(relative) }
                val matchedExclude = excludePatterns.firstOrNull { (regex, _) -> regex.matches(relative) }
                when {
                    matchedInclude == null -> notify?.onUnmatched(relative)
                    matchedExclude != null -> notify?.onExclude(relative, matchedExclude.second)
                    else -> {
                        results.add(file)
                        notify?.onInclude(relative, matchedInclude.second)
                    }
                }
                return FileVisitResult.CONTINUE
            }
        })

        return results
    }

    private fun normalizedRelative(baseDir: Path, path: Path): String =
        baseDir.relativize(path).toString().replace('\\', '/')
}
