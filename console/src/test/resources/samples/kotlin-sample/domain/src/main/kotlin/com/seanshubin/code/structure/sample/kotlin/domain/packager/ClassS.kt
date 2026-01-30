package com.seanshubin.code.structure.sample.kotlin.domain.packager

import com.seanshubin.code.structure.sample.kotlin.domain.packagei.ClassJ
import com.seanshubin.code.structure.sample.kotlin.domain.packaget.ClassU

class ClassS(
    private val j: ClassJ,
    private val u: ClassU
) {
    fun methodS() {
        j.methodJ()
        u.methodU()
    }
}
