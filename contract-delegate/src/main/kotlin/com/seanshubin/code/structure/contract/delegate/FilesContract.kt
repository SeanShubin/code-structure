package com.seanshubin.code.structure.contract.delegate

import java.io.*
import java.nio.channels.SeekableByteChannel
import java.nio.charset.Charset
import java.nio.file.*
import java.nio.file.attribute.*
import java.util.function.BiPredicate
import java.util.stream.Stream

interface FilesContract {
    fun newInputStream(path: Path, vararg options: OpenOption): InputStream
    fun newOutputStream(path: Path, vararg options: OpenOption): OutputStream
    fun newByteChannel(path: Path, options: Set<OpenOption>, vararg attrs: FileAttribute<*>): SeekableByteChannel
    fun newByteChannel(path: Path, vararg options: OpenOption): SeekableByteChannel
    fun newDirectoryStream(dir: Path): DirectoryStream<Path>
    fun newDirectoryStream(dir: Path, glob: String): DirectoryStream<Path>
    fun newDirectoryStream(dir: Path, filter: DirectoryStream.Filter<in Path>): DirectoryStream<Path>
    fun createFile(path: Path, vararg attrs: FileAttribute<*>): Path
    fun createDirectory(dir: Path, vararg attrs: FileAttribute<*>): Path
    fun createDirectories(dir: Path, vararg attrs: FileAttribute<*>): Path
    fun createTempFile(dir: Path, prefix: String, suffix: String, vararg attrs: FileAttribute<*>): Path
    fun createTempFile(prefix: String, suffix: String, vararg attrs: FileAttribute<*>): Path
    fun createTempDirectory(dir: Path, prefix: String, vararg attrs: FileAttribute<*>): Path
    fun createTempDirectory(prefix: String, vararg attrs: FileAttribute<*>): Path
    fun createSymbolicLink(link: Path, target: Path, vararg attrs: FileAttribute<*>): Path
    fun createLink(link: Path, existing: Path): Path
    fun delete(path: Path)
    fun deleteIfExists(path: Path): Boolean
    fun copy(source: Path, target: Path, vararg options: CopyOption): Path
    fun move(source: Path, target: Path, vararg options: CopyOption): Path
    fun readSymbolicLink(link: Path): Path
    fun getFileStore(path: Path): FileStore
    fun isSameFile(path: Path, path2: Path): Boolean
    fun isHidden(path: Path): Boolean
    fun probeContentType(path: Path): String
    fun <V : FileAttributeView> getFileAttributeView(path: Path, type: Class<V>, vararg options: LinkOption): V?
    fun <A : BasicFileAttributes> readAttributes(path: Path, type: Class<A>, vararg options: LinkOption): A
    fun setAttribute(path: Path, attribute: String, value: Any, vararg options: LinkOption): Path
    fun getAttribute(path: Path, attribute: String, vararg options: LinkOption): Any
    fun readAttributes(path: Path, attributes: String, vararg options: LinkOption): Map<String, Any>
    fun getPosixFilePermissions(path: Path, vararg options: LinkOption): Set<PosixFilePermission>
    fun setPosixFilePermissions(path: Path, perms: Set<PosixFilePermission>): Path
    fun getOwner(path: Path, vararg options: LinkOption): UserPrincipal
    fun setOwner(path: Path, owner: UserPrincipal): Path
    fun isSymbolicLink(path: Path): Boolean
    fun isDirectory(path: Path, vararg options: LinkOption): Boolean
    fun isRegularFile(path: Path, vararg options: LinkOption): Boolean
    fun getLastModifiedTime(path: Path, vararg options: LinkOption): FileTime
    fun setLastModifiedTime(path: Path, time: FileTime): Path
    fun size(path: Path): Long
    fun exists(path: Path, vararg options: LinkOption): Boolean
    fun notExists(path: Path, vararg options: LinkOption): Boolean
    fun isReadable(path: Path): Boolean
    fun isWritable(path: Path): Boolean
    fun isExecutable(path: Path): Boolean
    fun walkFileTree(start: Path, options: Set<FileVisitOption>, maxDepth: Int, visitor: FileVisitor<in Path>): Path
    fun walkFileTree(start: Path, visitor: FileVisitor<in Path>): Path
    fun newBufferedReader(path: Path, cs: Charset): BufferedReader
    fun newBufferedReader(path: Path): BufferedReader
    fun newBufferedWriter(path: Path, cs: Charset, vararg options: OpenOption): BufferedWriter
    fun newBufferedWriter(path: Path, vararg options: OpenOption): BufferedWriter
    fun copy(`in`: InputStream, target: Path, vararg options: CopyOption): Long
    fun copy(source: Path, out: OutputStream): Long
    fun readAllBytes(path: Path): ByteArray
    fun readString(path: Path): String
    fun readString(path: Path, cs: Charset): String
    fun readAllLines(path: Path, cs: Charset): List<String>
    fun readAllLines(path: Path): List<String>
    fun write(path: Path, bytes: ByteArray, vararg options: OpenOption): Path
    fun write(path: Path, lines: Iterable<CharSequence>, cs: Charset, vararg options: OpenOption): Path
    fun write(path: Path, lines: Iterable<CharSequence>, vararg options: OpenOption): Path
    fun writeString(path: Path, csq: CharSequence, vararg options: OpenOption): Path
    fun writeString(path: Path, csq: CharSequence, cs: Charset, vararg options: OpenOption): Path
    fun list(dir: Path): Stream<Path>
    fun walk(start: Path, vararg options: FileVisitOption): Stream<Path>
    fun walk(start: Path, maxDepth: Int, vararg options: FileVisitOption): Stream<Path>
    fun find(
        start: Path,
        maxDepth: Int,
        matcher: BiPredicate<Path, BasicFileAttributes>,
        vararg options: FileVisitOption
    ): Stream<Path>

    fun lines(path: Path, cs: Charset): Stream<String>
    fun lines(path: Path): Stream<String>
}
