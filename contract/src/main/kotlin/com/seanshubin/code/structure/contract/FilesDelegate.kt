package com.seanshubin.code.structure.contract

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStream
import java.io.OutputStream
import java.nio.channels.SeekableByteChannel
import java.nio.charset.Charset
import java.nio.file.*
import java.nio.file.attribute.*
import java.util.function.BiPredicate
import java.util.stream.Stream

object FilesDelegate:FilesContract {
    override fun newInputStream(path: Path, vararg options: OpenOption): InputStream =
        Files.newInputStream(path, *options)

    override fun newOutputStream(path: Path, vararg options: OpenOption): OutputStream =
        Files.newOutputStream(path, *options)

    override fun newByteChannel(
        path: Path,
        options: Set<OpenOption>,
        vararg attrs: FileAttribute<*>
    ): SeekableByteChannel =
        Files.newByteChannel(path, options, *attrs)

    override fun newByteChannel(path: Path, vararg options: OpenOption): SeekableByteChannel =
        Files.newByteChannel(path, *options)

    override fun newDirectoryStream(dir: Path): DirectoryStream<Path> =
        Files.newDirectoryStream(dir)

    override fun newDirectoryStream(dir: Path, glob: String): DirectoryStream<Path> =
        Files.newDirectoryStream(dir, glob)

    override fun newDirectoryStream(dir: Path, filter: DirectoryStream.Filter<in Path>): DirectoryStream<Path> =
        Files.newDirectoryStream(dir, filter)

    override fun createFile(path: Path, vararg attrs: FileAttribute<*>): Path =
        Files.createFile(path, *attrs)

    override fun createDirectory(dir: Path, vararg attrs: FileAttribute<*>): Path =
        Files.createDirectory(dir, *attrs)

    override fun createDirectories(dir: Path, vararg attrs: FileAttribute<*>): Path =
        Files.createDirectories(dir, *attrs)

    override fun createTempFile(dir: Path, prefix: String, suffix: String, vararg attrs: FileAttribute<*>): Path =
        Files.createTempFile(dir, prefix, suffix, *attrs)

    override fun createTempFile(prefix: String, suffix: String, vararg attrs: FileAttribute<*>): Path =
        Files.createTempFile(prefix, suffix, *attrs)

    override fun createTempDirectory(dir: Path, prefix: String, vararg attrs: FileAttribute<*>): Path =
        Files.createTempDirectory(dir, prefix, *attrs)

    override fun createTempDirectory(prefix: String, vararg attrs: FileAttribute<*>): Path =
        Files.createTempDirectory(prefix, *attrs)

    override fun createSymbolicLink(link: Path, target: Path, vararg attrs: FileAttribute<*>): Path =
        Files.createSymbolicLink(link, target, *attrs)

    override fun createLink(link: Path, existing: Path): Path =
        Files.createLink(link, existing)

    override fun delete(path: Path) =
        Files.delete(path)

    override fun deleteIfExists(path: Path): Boolean =
        Files.deleteIfExists(path)

    override fun copy(source: Path, target: Path, vararg options: CopyOption): Path =
        Files.copy(source, target, *options)

    override fun move(source: Path, target: Path, vararg options: CopyOption): Path =
        Files.move(source, target, *options)

    override fun readSymbolicLink(link: Path): Path =
        Files.readSymbolicLink(link)

    override fun getFileStore(path: Path): FileStore =
        Files.getFileStore(path)

    override fun isSameFile(path: Path, path2: Path): Boolean =
        Files.isSameFile(path, path2)

    override fun mismatch(path: Path, path2: Path): Long =
        Files.mismatch(path, path2)

    override fun isHidden(path: Path): Boolean =
        Files.isHidden(path)

    override fun probeContentType(path: Path): String =
        Files.probeContentType(path)

    override fun <V : FileAttributeView> getFileAttributeView(
        path: Path,
        type: Class<V>,
        vararg options: LinkOption
    ): V =
        Files.getFileAttributeView(path, type, *options)

    override fun <A : BasicFileAttributes> readAttributes(
        path: Path,
        type: Class<A>,
        vararg options: LinkOption
    ): A =
        Files.readAttributes(path, type, *options)

    override fun setAttribute(path: Path, attribute: String, value: Any, vararg options: LinkOption): Path =
        Files.setAttribute(path, attribute, value, *options)

    override fun getAttribute(path: Path, attribute: String, vararg options: LinkOption): Any =
        Files.getAttribute(path, attribute, *options)

    override fun readAttributes(path: Path, attributes: String, vararg options: LinkOption): Map<String, Any> =
        Files.readAttributes(path, attributes, *options)

    override fun getPosixFilePermissions(path: Path, vararg options: LinkOption): Set<PosixFilePermission> =
        Files.getPosixFilePermissions(path, *options)

    override fun setPosixFilePermissions(path: Path, perms: Set<PosixFilePermission>): Path =
        Files.setPosixFilePermissions(path, perms)

    override fun getOwner(path: Path, vararg options: LinkOption): UserPrincipal =
        Files.getOwner(path, *options)

    override fun setOwner(path: Path, owner: UserPrincipal): Path =
        Files.setOwner(path, owner)

    override fun isSymbolicLink(path: Path): Boolean =
        Files.isSymbolicLink(path)

    override fun isDirectory(path: Path, vararg options: LinkOption): Boolean =
        Files.isDirectory(path, *options)

    override fun isRegularFile(path: Path, vararg options: LinkOption): Boolean =
        Files.isRegularFile(path, *options)

    override fun getLastModifiedTime(path: Path, vararg options: LinkOption): FileTime =
        Files.getLastModifiedTime(path, *options)

    override fun setLastModifiedTime(path: Path, time: FileTime): Path =
        Files.setLastModifiedTime(path, time)

    override fun size(path: Path): Long =
        Files.size(path)

    override fun exists(path: Path, vararg options: LinkOption): Boolean =
        Files.exists(path, *options)

    override fun notExists(path: Path, vararg options: LinkOption): Boolean =
        Files.notExists(path, *options)

    override fun isReadable(path: Path): Boolean =
        Files.isReadable(path)

    override fun isWritable(path: Path): Boolean =
        Files.isWritable(path)

    override fun isExecutable(path: Path): Boolean =
        Files.isExecutable(path)

    override fun walkFileTree(
        start: Path,
        options: Set<FileVisitOption>,
        maxDepth: Int,
        visitor: FileVisitor<in Path>
    ): Path =
        Files.walkFileTree(start, options, maxDepth, visitor)

    override fun walkFileTree(start: Path, visitor: FileVisitor<in Path>): Path =
        Files.walkFileTree(start, visitor)

    override fun newBufferedReader(path: Path, cs: Charset): BufferedReader =
        Files.newBufferedReader(path, cs)

    override fun newBufferedWriter(path: Path, cs: Charset, vararg options: OpenOption): BufferedWriter =
        Files.newBufferedWriter(path, cs, *options)

    override fun newBufferedWriter(path: Path, vararg options: OpenOption): BufferedWriter =
        Files.newBufferedWriter(path, *options)

    override fun copy(`in`: InputStream, target: Path, vararg options: CopyOption): Long =
        Files.copy(`in`, target, *options)

    override fun copy(source: Path, out: OutputStream): Long =
        Files.copy(source, out)

    override fun readAllBytes(path: Path): ByteArray =
        Files.readAllBytes(path)

    override fun readString(path: Path, cs: Charset): String =
        Files.readString(path, cs)

    override fun readAllLines(path: Path, cs: Charset): List<String> =
        Files.readAllLines(path, cs)

    override fun write(path: Path, bytes: ByteArray, vararg options: OpenOption): Path =
        Files.write(path, bytes, *options)

    override fun write(path: Path, lines: Iterable<CharSequence>, cs: Charset, vararg options: OpenOption): Path =
        Files.write(path, lines, cs, *options)

    override fun write(path: Path, lines: Iterable<CharSequence>, vararg options: OpenOption): Path =
        Files.write(path, lines, *options)

    override fun writeString(path: Path, csq: CharSequence, vararg options: OpenOption): Path =
        Files.writeString(path, csq, *options)

    override fun writeString(path: Path, csq: CharSequence, cs: Charset, vararg options: OpenOption): Path =
        Files.writeString(path, csq, cs, *options)

    override fun list(dir: Path): Stream<Path> =
        Files.list(dir)

    override fun walk(start: Path, maxDepth: Int, vararg options: FileVisitOption): Stream<Path> =
        Files.walk(start, maxDepth, *options)

    override fun walk(start: Path, vararg options: FileVisitOption): Stream<Path> =
        Files.walk(start, *options)

    override fun find(
        start: Path,
        maxDepth: Int,
        matcher: BiPredicate<Path, BasicFileAttributes>,
        vararg options: FileVisitOption
    ): Stream<Path> =
        Files.find(start, maxDepth, matcher, *options)

    override fun lines(path: Path, cs: Charset): Stream<String> =
        Files.lines(path, cs)
}
