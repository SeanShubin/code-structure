package com.seanshubin.code.structure.contract.delegate

import java.io.*
import java.nio.channels.SeekableByteChannel
import java.nio.charset.Charset
import java.nio.file.*
import java.nio.file.attribute.*
import java.util.function.BiPredicate
import java.util.stream.Stream

interface FilesContract {
    @Throws(IOException::class)
    fun newInputStream(path: Path, vararg options: OpenOption): InputStream

    @Throws(IOException::class)
    fun newOutputStream(path: Path, vararg options: OpenOption): OutputStream

    @Throws(IOException::class)
    fun newByteChannel(
        path: Path,
        options: Set<OpenOption>,
        vararg attrs: FileAttribute<*>
    ): SeekableByteChannel

    @Throws(IOException::class)
    fun newByteChannel(path: Path, vararg options: OpenOption): SeekableByteChannel

    @Throws(IOException::class)
    fun newDirectoryStream(dir: Path): DirectoryStream<Path>

    @Throws(IOException::class)
    fun newDirectoryStream(dir: Path, glob: String): DirectoryStream<Path>

    @Throws(IOException::class)
    fun newDirectoryStream(
        dir: Path,
        filter: DirectoryStream.Filter<in Path>
    ): DirectoryStream<Path>

    @Throws(IOException::class)
    fun createFile(path: Path, vararg attrs: FileAttribute<*>): Path

    @Throws(IOException::class)
    fun createDirectory(dir: Path, vararg attrs: FileAttribute<*>): Path

    @Throws(IOException::class)
    fun createDirectories(dir: Path, vararg attrs: FileAttribute<*>): Path

    @Throws(IOException::class)
    fun createTempFile(
        dir: Path,
        prefix: String,
        suffix: String,
        vararg attrs: FileAttribute<*>
    ): Path

    @Throws(IOException::class)
    fun createTempFile(
        prefix: String,
        suffix: String,
        vararg attrs: FileAttribute<*>
    ): Path

    @Throws(IOException::class)
    fun createTempDirectory(
        dir: Path,
        prefix: String,
        vararg attrs: FileAttribute<*>
    ): Path

    @Throws(IOException::class)
    fun createTempDirectory(
        prefix: String,
        vararg attrs: FileAttribute<*>
    ): Path

    @Throws(IOException::class)
    fun createSymbolicLink(
        link: Path,
        target: Path,
        vararg attrs: FileAttribute<*>
    ): Path

    @Throws(IOException::class)
    fun createLink(link: Path, existing: Path): Path

    @Throws(IOException::class)
    fun delete(path: Path)

    @Throws(IOException::class)
    fun deleteIfExists(path: Path): Boolean

    @Throws(IOException::class)
    fun copy(source: Path, target: Path, vararg options: CopyOption): Path

    @Throws(IOException::class)
    fun move(source: Path, target: Path, vararg options: CopyOption): Path

    @Throws(IOException::class)
    fun readSymbolicLink(link: Path): Path

    @Throws(IOException::class)
    fun getFileStore(path: Path): FileStore

    @Throws(IOException::class)
    fun isSameFile(path: Path, path2: Path): Boolean

    @Throws(IOException::class)
    fun mismatch(path: Path, path2: Path): Long

    @Throws(IOException::class)
    fun isHidden(path: Path): Boolean

    @Throws(IOException::class)
    fun probeContentType(path: Path): String

    fun <V : FileAttributeView> getFileAttributeView(
        path: Path,
        type: Class<V>,
        vararg options: LinkOption
    ): V

    @Throws(IOException::class)
    fun <A : BasicFileAttributes> readAttributes(
        path: Path,
        type: Class<A>,
        vararg options: LinkOption
    ): A

    @Throws(IOException::class)
    fun setAttribute(
        path: Path, attribute: String, value: Any,
        vararg options: LinkOption
    ): Path

    @Throws(IOException::class)
    fun getAttribute(
        path: Path, attribute: String,
        vararg options: LinkOption
    ): Any

    @Throws(IOException::class)
    fun readAttributes(
        path: Path, attributes: String,
        vararg options: LinkOption
    ): Map<String, Any>

    @Throws(IOException::class)
    fun getPosixFilePermissions(
        path: Path,
        vararg options: LinkOption
    ): Set<PosixFilePermission>

    @Throws(IOException::class)
    fun setPosixFilePermissions(
        path: Path,
        perms: Set<PosixFilePermission>
    ): Path

    @Throws(IOException::class)
    fun getOwner(path: Path, vararg options: LinkOption): UserPrincipal

    @Throws(IOException::class)
    fun setOwner(path: Path, owner: UserPrincipal): Path

    fun isSymbolicLink(path: Path): Boolean

    fun isDirectory(path: Path, vararg options: LinkOption): Boolean

    fun isRegularFile(path: Path, vararg options: LinkOption): Boolean

    @Throws(IOException::class)
    fun getLastModifiedTime(path: Path, vararg options: LinkOption): FileTime

    @Throws(IOException::class)
    fun setLastModifiedTime(path: Path, time: FileTime): Path

    @Throws(IOException::class)
    fun size(path: Path): Long

    fun exists(path: Path, vararg options: LinkOption): Boolean

    fun notExists(path: Path, vararg options: LinkOption): Boolean

    fun isReadable(path: Path): Boolean

    fun isWritable(path: Path): Boolean

    fun isExecutable(path: Path): Boolean

    @Throws(IOException::class)
    fun walkFileTree(
        start: Path,
        options: Set<FileVisitOption>,
        maxDepth: Int,
        visitor: FileVisitor<in Path>
    ): Path

    @Throws(IOException::class)
    fun walkFileTree(start: Path, visitor: FileVisitor<in Path>): Path

    @Throws(IOException::class)
    fun newBufferedReader(path: Path, cs: Charset): BufferedReader

    @Throws(IOException::class)
    fun newBufferedWriter(
        path: Path, cs: Charset,
        vararg options: OpenOption
    ): BufferedWriter

    @Throws(IOException::class)
    fun newBufferedWriter(path: Path, vararg options: OpenOption): BufferedWriter

    @Throws(IOException::class)
    fun copy(`in`: InputStream, target: Path, vararg options: CopyOption): Long

    @Throws(IOException::class)
    fun copy(source: Path, out: OutputStream): Long

    @Throws(IOException::class)
    fun readAllBytes(path: Path): ByteArray

    @Throws(IOException::class)
    fun readString(path: Path, cs: Charset): String

    @Throws(IOException::class)
    fun readAllLines(path: Path, cs: Charset): List<String>

    @Throws(IOException::class)
    fun write(path: Path, bytes: ByteArray, vararg options: OpenOption): Path

    @Throws(IOException::class)
    fun write(
        path: Path, lines: Iterable<CharSequence>,
        cs: Charset, vararg options: OpenOption
    ): Path

    @Throws(IOException::class)
    fun write(
        path: Path,
        lines: Iterable<CharSequence>,
        vararg options: OpenOption
    ): Path

    @Throws(IOException::class)
    fun writeString(path: Path, csq: CharSequence, vararg options: OpenOption): Path

    @Throws(IOException::class)
    fun writeString(path: Path, csq: CharSequence, cs: Charset, vararg options: OpenOption): Path

    @Throws(IOException::class)
    fun list(dir: Path): Stream<Path>

    @Throws(IOException::class)
    fun walk(
        start: Path,
        maxDepth: Int,
        vararg options: FileVisitOption
    ): Stream<Path>

    @Throws(IOException::class)
    fun walk(start: Path, vararg options: FileVisitOption): Stream<Path>

    @Throws(IOException::class)
    fun find(
        start: Path,
        maxDepth: Int,
        matcher: BiPredicate<Path, BasicFileAttributes>,
        vararg options: FileVisitOption
    ): Stream<Path>

    @Throws(IOException::class)
    fun lines(path: Path, cs: Charset): Stream<String>
}
