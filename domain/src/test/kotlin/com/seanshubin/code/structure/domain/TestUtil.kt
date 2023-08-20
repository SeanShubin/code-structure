package com.seanshubin.code.structure.domain

import kotlin.test.assertEquals

object TestUtil {
    fun <T> List<T>.exactlyOne(): T {
        assertEquals(1, this.size)
        return get(0)
    }
}
