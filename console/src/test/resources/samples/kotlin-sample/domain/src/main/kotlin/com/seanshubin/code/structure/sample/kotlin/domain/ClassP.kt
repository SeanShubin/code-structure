package com.seanshubin.code.structure.sample.kotlin.domain

import com.seanshubin.code.structure.sample.kotlin.domain.packagep.ClassQ

class ClassP(private val q: ClassQ) {
    fun methodP() {
        q.methodQ()
    }
}
