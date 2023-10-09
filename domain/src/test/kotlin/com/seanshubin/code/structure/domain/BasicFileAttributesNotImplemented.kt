package com.seanshubin.code.structure.domain

import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime

abstract class BasicFileAttributesNotImplemented : BasicFileAttributes {
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
