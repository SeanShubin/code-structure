package com.seanshubin.code.structure.collection

data class SetCompareResult<T>(
    val missing: Set<T>,
    val extra: Set<T>,
    val same: Set<T>
) {
    fun isSame():Boolean = missing.isEmpty() && extra.isEmpty()
}
