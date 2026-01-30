package com.seanshubin.code.structure.sample.kotlin.domain.packagei

import com.seanshubin.code.structure.sample.kotlin.domain.packagep.ClassQ

class ClassJ(private val q: ClassQ) {
    fun methodJ() {
        q.methodQ()
    }
}
