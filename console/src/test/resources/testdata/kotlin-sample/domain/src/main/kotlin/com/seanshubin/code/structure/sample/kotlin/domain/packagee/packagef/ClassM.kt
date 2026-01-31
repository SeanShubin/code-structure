package com.seanshubin.code.structure.sample.kotlin.domain.packagee.packagef

import com.seanshubin.code.structure.sample.kotlin.domain.ClassE

class ClassM(
    private val n: ClassN,
    private val e: ClassE
) {
    fun methodM() {
        n.methodN()
        e.methodE()
    }
}
