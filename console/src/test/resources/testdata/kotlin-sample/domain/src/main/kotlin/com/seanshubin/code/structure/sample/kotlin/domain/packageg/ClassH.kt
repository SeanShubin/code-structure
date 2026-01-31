package com.seanshubin.code.structure.sample.kotlin.domain.packageg

import com.seanshubin.code.structure.sample.kotlin.domain.packagec.ClassD
import com.seanshubin.code.structure.sample.kotlin.domain.packagei.ClassJ

class ClassH(
    private val d: ClassD,
    private val j: ClassJ,
) {
    fun methodH() {
        d.methodD()
        j.methodJ()
    }
}
