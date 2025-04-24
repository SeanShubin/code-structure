package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.contract.test.FilesContractUnsupportedOperation
import com.seanshubin.code.structure.tree.Tree
import java.nio.charset.Charset
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.util.function.BiPredicate
import java.util.stream.Stream

class FakeFiles : FilesContractUnsupportedOperation {
    var root: Tree<String, String> = Tree.empty()
    fun fakeAddFile(pathName: String, contents: String) {
        val path = Paths.get(pathName)
        val pathParts = path.toList().map { it.toString() }
        root = root.setValue(pathParts, contents)
    }

    override fun writeString(path: Path, csq: CharSequence, cs: Charset, vararg options: OpenOption): Path {
        throw UnsupportedOperationException("not implemented")
    }

    override fun readString(path: Path, cs: Charset): String {
        return root.getValue(path.toKey()) ?: throw RuntimeException("No value at $path")
    }

    override fun createDirectories(dir: Path, vararg attrs: FileAttribute<*>): Path {
        throw UnsupportedOperationException("not implemented")
    }

    override fun exists(path: Path, vararg options: LinkOption): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    override fun find(
        start: Path,
        maxDepth: Int,
        matcher: BiPredicate<Path, BasicFileAttributes>,
        vararg options: FileVisitOption
    ): Stream<Path> {
        val result = root.pathValues(emptyList()).map { (pathParts, _) ->
            Paths.get(pathParts[0], *pathParts.drop(1).toTypedArray())
        }.filter { path ->
            matcher.test(path, fileAttributesNotImplemented)
        }
        return result.stream()
    }

    private val fileAttributesNotImplemented = object : BasicFileAttributesNotImplemented() {
    }

    private fun Path.toKey(): List<String> =
        iterator().asSequence().map { it.toString() }.toList()
}
