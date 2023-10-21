package com.seanshubin.code.structure.collection

object SetUtil {
    fun <T> compare(old:Set<T>, current:Set<T>):SetCompareResult<T>{
        val same = old intersect current
        val missing = old - current
        val extra = current - old
        return SetCompareResult(missing, extra, same)
    }
}