package com.seanshubin.code.structure.filefinder

import java.util.function.BiPredicate

class BiPredicateAdapterUseFirst<T, U>(val delegate: (T) -> Boolean) : BiPredicate<T, U> {
    override fun test(t: T, u: U): Boolean = delegate(t)
}
