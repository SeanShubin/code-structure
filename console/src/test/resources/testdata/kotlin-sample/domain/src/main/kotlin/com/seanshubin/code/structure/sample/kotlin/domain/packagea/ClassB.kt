package com.seanshubin.code.structure.sample.kotlin.domain.packagea

import com.seanshubin.code.structure.sample.kotlin.domain.packagec.ClassD

class ClassB(private val d: ClassD) {
    fun methodB() {
        d.methodD()
    }
}
