package com.seanshubin.code.structure.domain

import com.seanshubin.code.structure.contract.test.FilesNotImplemented
import com.seanshubin.code.structure.tree.Tree
import java.nio.charset.Charset
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileTime
import java.util.function.BiPredicate
import java.util.stream.Stream

class FakeFiles:FilesNotImplemented() {
    var root:Tree<String, String> = Tree.empty()
    fun fakeAddFile(pathName:String, contents:String){
        val path = Paths.get(pathName)
        val pathParts = path.toList().map{it.toString()}
        root = root.setValue(pathParts, contents)
    }
    override fun writeString(path: Path, csq: CharSequence, cs: Charset, vararg options: OpenOption): Path {
        throw UnsupportedOperationException("not implemented")
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
        val result = root.pathValues(emptyList()).map{ (pathParts, _) ->
            Paths.get(pathParts[0], *pathParts.drop(1).toTypedArray())
        }.filter{ path ->
            matcher.test(path, fileAttributesNotImplemented)
        }
        return result.stream()
    }

    val fileAttributesNotImplemented = object:BasicFileAttributes{
        override fun lastModifiedTime(): FileTime {
            throw UnsupportedOperationException("not implemented")
        }

        override fun lastAccessTime(): FileTime {
            throw UnsupportedOperationException("not implemented")
        }

        override fun creationTime(): FileTime {
            throw UnsupportedOperationException("not implemented")
        }

        override fun isRegularFile(): Boolean {
            throw UnsupportedOperationException("not implemented")
        }

        override fun isDirectory(): Boolean {
            throw UnsupportedOperationException("not implemented")
        }

        override fun isSymbolicLink(): Boolean {
            throw UnsupportedOperationException("not implemented")
        }

        override fun isOther(): Boolean {
            throw UnsupportedOperationException("not implemented")
        }

        override fun size(): Long {
            throw UnsupportedOperationException("not implemented")
        }

        override fun fileKey(): Any {
            throw UnsupportedOperationException("not implemented")
        }
    }
}
